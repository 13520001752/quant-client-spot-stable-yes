package com.magic.service;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.binance.connector.client.SpotClient;
import com.binance.connector.client.WebSocketStreamClient;
import com.binance.connector.client.impl.SpotClientImpl;
import com.binance.connector.client.impl.WebSocketStreamClientImpl;
import com.magic.cache.redis.RedisCacheUtils;
import com.magic.constant.Constants;
import com.magic.emum.BizErrorEnum;
import com.magic.entity.BinanceMiniTicker;
import com.magic.mybatisplus.entity.TConfig;
import com.magic.mybatisplus.entity.TSymbolConfig;
import com.magic.mybatisplus.mapper.TConfigMapper;
import com.magic.mybatisplus.mapper.TOrderMapper;
import com.magic.mybatisplus.mapper.TSymbolConfigMapper;
import com.magic.mybatisplus.service.TOrderService;
import com.magic.vo.resp.base.ResponseBase;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class QuantTask {
    public final int FlowTypeStart   = 0;
    public final int FlowTypeRunning = 1;
    public final int FlowTypeStop    = 2;

    private RedisCacheUtils          redisClient;
    private TOrderService            orderService;
    private HelperBinanceAccountInfo helperBinanceAccountInfo;
    private TConfigMapper            configMapper;

    public static AtomicInteger threadSeqId = new AtomicInteger(0);

    // 100.myquant@gmail.com
    // 1-100-myquant
    private String apiKey    = "BQPPirmuZHFuMWue3etlTzsHGuVqfiyvi3hsi3PMgYf3Ue17v729jA4ssg78sgrJ";
    private String apiSecret = "dRpCKyjKSWsr50M0ZutjtGRWlCfEwpBTclgF3bSPSLICUtyvWeiFihgXGEykWjje";

    // ------------------
    // symbol, BinancePriceMark
    public ConcurrentHashMap<String, BinancePosition>    hashMapPosition = new ConcurrentHashMap<>();
    public ConcurrentHashMap<String, BinanceBalanceSpot> hashMapBalance  = new ConcurrentHashMap<>();

    // symbol, price: 量化订单最新成交价
    public ConcurrentHashMap<String, BigDecimal> hashTradePriceLast = new ConcurrentHashMap<>();

    public volatile Thread threadListenMonitor;
    public volatile String ListenKey;
    public volatile long   listenKeyCreatedAt  = 0L;
    public volatile long   listenKeyExtendedAt = 0L;

    public AtomicReference<String> listenStatus      = new AtomicReference<>(Constants.LISTEN_STATUS_STOPPED);
    public AtomicInteger           listenKeyStreamId = new AtomicInteger(-1);

    private volatile boolean initDone = false;
    private volatile boolean doExit   = false;

    // 最后一次更新状态的时间戳
    private volatile long tsStatusUpdate = 0L;

    private volatile Semaphore             semaphoreFlowLock = new Semaphore(0);
    private volatile TConfig               config            = null;
    private volatile TSymbolConfig         symbolConfig      = null;
    private volatile SpotClient            clientHTTP        = null;
    private volatile SpotClient            clientAccountHTTP = null;
    private volatile WebSocketStreamClient clientWebSocket   = null;

    LarkService larkService;

    private final ConcurrentHashMap<String, BinanceMiniTicker> mapMiniTicker     = new ConcurrentHashMap<>();
    private       AtomicInteger                                countIdMiniTicker = new AtomicInteger(0);

    // 余额推送晚于订单推送, 这里临时保存，通过余变动触发下单
    LinkedList<JSONObject> listOrderUpdate = new LinkedList<>();

    public QuantTask(TConfig config, TSymbolConfig symbolConfig, TOrderService orderService, RedisCacheUtils redisClient, TConfigMapper configMapper, LarkService larkService) {
        this.config       = config;
        this.symbolConfig = symbolConfig;
        this.configMapper = configMapper;

        this.redisClient              = redisClient;
        this.orderService             = orderService;
        this.helperBinanceAccountInfo = new HelperBinanceAccountInfo(config, symbolConfig);
        this.larkService              = larkService;
    }

    public synchronized ResponseBase start(TConfig config, TSymbolConfig symbolConfig) {
        if (clientHTTP == null || clientWebSocket == null || clientAccountHTTP == null) {
            clientHTTP        = new SpotClientImpl();
            clientAccountHTTP = new SpotClientImpl(config.getApiKey(), config.getApiSecret());
            clientWebSocket   = new WebSocketStreamClientImpl();
        }

        JSONObject joConfig = new JSONObject(config);
        JSONObject joSymbol = new JSONObject(symbolConfig);

        log.info("start parameter config:{}", joConfig.toStringPretty());
        log.info("start parameter symbol:{}", joSymbol.toStringPretty());

        // TODO: 参数检查
        // TODO: 账号配置检查, 单项持仓，全仓，合约权限...
        // scalePrice = symbolConfig.scalePrice

        String account = clientAccountHTTP.createTrade().account(null);
        if (account != null) {
            long       ts = System.currentTimeMillis();
            JSONObject jo = new JSONObject(account);
            JSONArray  ja = jo.getJSONArray("balances");

            if (ja != null && !ja.isEmpty()) {
                for (int i = 0; i < ja.size(); i++) {
                    JSONObject jo2 = ja.getJSONObject(i);

                    BinanceBalanceSpot balance = new BinanceBalanceSpot(
                            jo2.getStr("asset"),
                            jo2.getBigDecimal("free"),
                            jo2.getBigDecimal("locked"),
                            ts
                    );

                    if (balance.getFree() == null || balance.getLocked() == null) {
                        continue;
                    }

                    if (balance.getFree().compareTo(BigDecimal.ZERO) != 1 && balance.getLocked().compareTo(BigDecimal.ZERO) != 1) {
                        continue;
                    }

                    hashMapBalance.put(balance.getAsset(), balance);
                }
            }
        }

        // 订阅持仓 & 余额变化
        threadListenMonitor = new Thread(() -> {
            try {
                threadListenMonitor(clientAccountHTTP, clientWebSocket);
            } catch (Exception e) {
                log.error("threadListenMonitor failed to spawn, configId:{}, exception:", config.getId(), e);
            }
        });
        threadListenMonitor.setName("threadListen-" + threadSeqId.getAndIncrement());
        threadListenMonitor.start();

        try {
            log.info("threadListenMonitor is not ready, wait, semaphoreFlowLock:{}", semaphoreFlowLock.availablePermits());
            semaphoreFlowLock.acquire(1);
        } catch (Exception e) {
            log.error("threadListenMonitor wait exception:", e);
            return ResponseBase.fail(BizErrorEnum.FAILED_LISTEN_START);
        }
        log.info("threadListenMonitor is ready, continue, semaphoreFlowLock:{}", semaphoreFlowLock.availablePermits());

        initDone = true;
        return ResponseBase.GetResponseSuccess();
    }

    public ResponseBase stop() {
        initDone = false;
        doExit   = true;

        // 监听线程，停止完毕
        try {
            log.info("stop thread exit: wait, semaphoreFlowLock: {}", this.semaphoreFlowLock.availablePermits());
            this.semaphoreFlowLock.acquire();
            log.info("stop thread exit: done, threadIsAlive:{}, semaphoreFlowLock:{}", threadListenMonitor.isAlive(), semaphoreFlowLock.availablePermits());
        } catch (InterruptedException e) {
            log.error("stop thread exit: exception:", e);
        }

        try {
            log.info("stop thread join: semaphoreFlowLock: {}", this.semaphoreFlowLock.availablePermits());
            threadListenMonitor.join(10 * 1000);
            log.info("stop thread join: threadIsAlive:{}, semaphoreFlowLock:{}", threadListenMonitor.isAlive(), semaphoreFlowLock.availablePermits());
        } catch (InterruptedException e) {
            log.error("stop thread join exception:", e);
        }

        clientHTTP        = null;
        clientWebSocket   = null;
        clientAccountHTTP = null;

        log.info("stop done");
        return ResponseBase.GetResponseSuccess();
    }

    // 定时写入ping time
    private void TaskStatusUpdate() {
        Long tsNow = System.currentTimeMillis();

        // 3 秒更新一次
        if (tsNow - tsStatusUpdate < 3 * 1000) {
            return;
        }
        tsStatusUpdate = tsNow;

        boolean updateRet = false;
        try {
            //            Calendar ca = Calendar.getInstance();
            //            TimeZone tz = ca.getTimeZone();

            //            LocalDateTime now = LocalDateTime.now(ZoneId.systemDefault());
            long ts = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

            //            log.info("TZ Id:{}, name:{}, now:{}", tz.getID(), tz.getDisplayName(), now);

            updateRet = new LambdaUpdateChainWrapper<>(configMapper).eq(TConfig::getId, config.getId()).set(TConfig::getPingTime, ts).update();
        } catch (Exception e) {
            log.error("TaskStatusUpdate exception:", e);
        }
        // save last match price
    }

    private int threadListenMonitor(SpotClient clientAccountHTTP, WebSocketStreamClient clientWebSocket) {
        String        resp               = null;
        JSONObject    jo                 = null;
        boolean       listenKeyReCreated = false;
        AtomicInteger flowType           = new AtomicInteger(FlowTypeStart);

        log.info("threadListen started");

        while (!doExit) {
            TaskStatusUpdate();

            // 发生OnError，  状态被置为: LISTEN_STATUS_ERROR
            // 发生OnClosing，状态被置为: LISTEN_STATUS_STOPPED
            switch (listenStatus.get()) {
                case Constants.LISTEN_STATUS_STOPPED:
                case Constants.LISTEN_STATUS_ERROR:
                    ListenKey = null;
                    break;
            }

            if (ListenKey == null) {
                try {
                    String ListenKeyOld = ListenKey;

                    resp                = clientAccountHTTP.createUserData().createListenKey();
                    jo                  = new JSONObject(resp);
                    ListenKey           = jo.getStr("listenKey");
                    listenKeyCreatedAt  = System.currentTimeMillis();
                    listenKeyExtendedAt = System.currentTimeMillis();
                    listenKeyReCreated  = true;

                    log.info("threadListen createListenKey key:{} -> {}, createdAt:{}, updatedAt:{}, resp:{}", ListenKeyOld, ListenKey, listenKeyCreatedAt, listenKeyExtendedAt, resp);
                } catch (Exception e) {
                    ListenKey = null;

                    log.error("threadListen createListenKey failed, configId:{}, exception:", config.getId(), e);
                    larkService.SendSubscriptionEvent(config, LarkService.UserDataListenKeyCreateFailed);
                    QuantUtil.waitMs(1000);
                    continue;
                }

//                larkService.SendSubscriptionEvent(config, LarkService.UserDataListenKeyCreateSuccess);
            } else {
                final long tsMax = 10 * 60 * 1000;
                long       tsNow = System.currentTimeMillis();
                long       tsGap = tsNow - listenKeyExtendedAt;

                if (tsGap >= tsMax) {
                    try {
                        Map<String, Object> parameters = new LinkedHashMap<>();
                        parameters.put("listenKey", ListenKey);

                        resp = clientAccountHTTP.createUserData().extendListenKey(parameters);
                    } catch (Exception e) {
                        ListenKey = null;

                        log.error("threadListen extendListenKey failed, configId:{}, resp:{}, exception:", config.getId(), resp, e);
                        QuantUtil.waitMs(200);
                        continue;
                    }

                    // 成功
                    if ("{}".equals(resp)) {
                        listenKeyExtendedAt = System.currentTimeMillis();
                        log.info("threadListen extendListenKey success, configId:{}, resp:{}, key:{}", config.getId(), resp, ListenKey);
                        // larkService.SendSubscriptionEvent(config, LarkService.UserDataListenKeyExtendSuccess);
                    } else {
                        log.info("threadListen extendListenKey failed, doCreate configId:{} ListenKey:{} -> null", config.getId(), ListenKey);
                        ListenKey = null;   // << 重新创建listenKey
                        larkService.SendSubscriptionEvent(config, LarkService.UserDataListenKeyExtendFailed);
                        continue;
                    }
                }
            }

            switch (listenStatus.get()) {
                case Constants.LISTEN_STATUS_STOPPED:
                case Constants.LISTEN_STATUS_ERROR:
                    // need reset: yes
                    break;
                default:
                    // 重新创建的ListenKey, 必须重新订阅
                    // 延期listenKey失败，走这个逻辑
                    if (listenKeyReCreated) {
                        break;
                    }
                    // need reset: no
                    QuantUtil.waitMs(500);
                    continue;
            }
            listenKeyReCreated = false;

            // close previous stream if exist
            if (listenKeyStreamId.get() != -1) {
                try {
                    clientWebSocket.closeConnection(listenKeyStreamId.get());
                    log.info("threadListen close streamId:{}", listenKeyStreamId);
                } catch (Exception e) {
                    log.error("threadListen close streamId failed, streamId:{}, exception:", listenKeyStreamId, e);
                    continue;
                } finally {
                    log.info("threadListen close streamId done, streamId:{}", listenKeyStreamId);
                    listenKeyStreamId.set(-1);
                }
            }

            try {
                listenStatus.set(Constants.LISTEN_STATUS_STARTING);
                log.info("threadListen start to listenUserStream, configId:{}", config.getId());

                ArrayList<String> streams = new ArrayList<>();
                streams.add("!miniTicker@arr");
                streams.add(ListenKey);

                log.info("threadListen subscribe topic:{}", streams.toString());

                int listenKeyStreamIdTmp = clientWebSocket.combineStreams(streams, (onOpenMsg) -> {
                    log.info("threadListen onOpen1, status:{}->{}, key:{}/{}, semaphoreFlowLock:{}", listenStatus, Constants.LISTEN_STATUS_RUNNING, listenKeyStreamId, ListenKey, semaphoreFlowLock.availablePermits());
                    listenStatus.set(Constants.LISTEN_STATUS_RUNNING);

                    if (flowType.intValue() == FlowTypeStart) {
                        flowType.set(FlowTypeRunning);

                        semaphoreFlowLock.release();
                        log.info("threadListen onOpen3, status:{}->{}, key:{}/{}, semaphoreFlowLock:{}", listenStatus, Constants.LISTEN_STATUS_RUNNING, listenKeyStreamId, ListenKey, semaphoreFlowLock.availablePermits());
                    }

//                    larkService.SendSubscriptionEvent(config, LarkService.UserDataSubOnOpen);
                }, (onMsg) -> {
                    handle(clientAccountHTTP, hashMapBalance, hashMapPosition, hashTradePriceLast, onMsg);
                }, (onClosingParam1, onClosingParam2) -> {
                    log.info("threadListen onClosing, status:{}->{}, key:{}/{}, param1:{}, param2:{}", listenStatus, listenStatus, listenKeyStreamId, ListenKey, onClosingParam1, onClosingParam2);
                }, (onCloseParam1, onCloseParam2) -> {
                    log.info("threadListen onClose, status:{}->{}, key:{}/{}", listenStatus, listenStatus, listenKeyStreamId, ListenKey);

                    // listenStatus = Constants.LISTEN_STATUS_STOPPED;
//                    larkService.SendSubscriptionEvent(config, LarkService.UserDataSubOnClose);
                }, (errorParam1, errorParam2) -> {
                    // 之前发生onError时，没有onClose, 因此在这里先关闭，再观察情况
                    log.error("threadListen onError, status:{}, key:{}/{}, error1:{}, error2:{}", listenStatus, listenKeyStreamId, ListenKey, errorParam1, errorParam2);

                    // 客服回复，onError由服务端触发，因此客户端需要重新订阅
                    listenStatus.set(Constants.LISTEN_STATUS_ERROR);
                    // larkService.SendSubscriptionEvent(config, LarkService.UserDataSubOnFailure);
                });
                listenKeyStreamId.set(listenKeyStreamIdTmp);
            } catch (Exception e) {
                log.error("threadListen onException, status{}->{}, key:{}/{}", listenStatus, Constants.LISTEN_STATUS_ERROR, listenKeyStreamId, ListenKey);
                log.error("threadListen onException", e);

                listenStatus.set(Constants.LISTEN_STATUS_ERROR);

                larkService.SendSubscriptionEvent(config, LarkService.UserDataSubFailed);
                log.error("threadListen onException, wait 1000");
                QuantUtil.waitMs(1000);
                continue;
            }

            // 启动中，等待状态变更
            // starting -> running
            // starting -> error
            // starting -> stopped
            while (listenStatus.get().equalsIgnoreCase(Constants.LISTEN_STATUS_STARTING)) {
                QuantUtil.waitMs(500);
                log.info("threadListen pooling status, key:{}/{}", listenKeyStreamId, ListenKey);
            }

            switch (listenStatus.get()) {
                case Constants.LISTEN_STATUS_RUNNING:
                    log.info("threadListen start success in running, key:{}/{}", listenKeyStreamId, ListenKey);
                    break;
                case Constants.LISTEN_STATUS_STOPPED:
                    log.info("threadListen start failed in stopped, key:{}/{}", listenKeyStreamId, ListenKey);
                    QuantUtil.waitMs(500);
                    break;
                case Constants.LISTEN_STATUS_ERROR:
                    log.info("threadListen start failed in error, key:{}/{}", listenKeyStreamId, ListenKey);
                    QuantUtil.waitMs(500);
                    break;
            }
        }

        log.info("threadListen listenKeyStreamId:{}", listenKeyStreamId);
        if (listenKeyStreamId.get() > 0) {
            try {
                clientWebSocket.closeConnection(listenKeyStreamId.get());
            } catch (Exception e) {
                log.error("threadListen exception in closing listenKeyStreamId:{} -> -1", listenKeyStreamId, e);
            }
            log.info("threadListen close listenKeyStreamId:{} -> -1", listenKeyStreamId);
            listenKeyStreamId.set(-1);
        }

        log.info("threadListen doExit....");

        // 不能使用：clientWebSocket.closeAllConnections()
        // 使用后，无法创建新的websocket连接，原因未知

        ListenKey = null;
        listenKeyStreamId.set(-1);
        listenStatus.set(Constants.LISTEN_STATUS_STOPPED);

        log.info("threadListen exit1, semaphoreFlowLock:{}", semaphoreFlowLock.availablePermits());
        semaphoreFlowLock.release();
        log.info("threadListen exit2, semaphoreFlowLock:{}", semaphoreFlowLock.availablePermits());

        larkService.SendSubscriptionEvent(config, LarkService.UserDataSubExit);
        return 0;
    }

    public int handle(SpotClient clientAccountHTTP, ConcurrentHashMap<String, BinanceBalanceSpot> hashMapBalance2, ConcurrentHashMap<String, BinancePosition> hashMapPosition, ConcurrentHashMap<String, BigDecimal> hashTradePriceLast, String msg) {
        JSONObject jo = new JSONObject(msg);

        // {
        //    "stream": "!miniTicker@arr",
        //    "data": [
        //        {
        //            "e": "24hrMiniTicker",
        //            "E": 1710578035986,
        //            "s": "MDTUSDT",
        //            "c": "0.1262900",
        //            "o": "0.1214100",
        //            "h": "0.1325100",
        //            "l": "0.1100000",
        //            "v": "551881181",
        //            "q": "67645464.4528300"
        //        }
        //    ]
        //}


        String stream = jo.getStr("stream");

        if ("!miniTicker@arr".equalsIgnoreCase(stream)) {
            JSONArray jaData = jo.getJSONArray("data");
            if (jaData == null || jaData.isEmpty()) {
                log.error("");
                return 0;
            }

            String event = null;
            for (int i = 0; i < jaData.size(); i++) {
                jo = jaData.getJSONObject(i);

                event = jo.getStr("e");
                Long       tsMatch      = jo.getLong("T");
                JSONObject eventAccount = jo.getJSONObject("a");    // 账户更新事件

                switch (event) {
                    case "24hrMiniTicker":
                        handleMarket24HrMiniTicker(jo);
                        break;
                    default:
                        log.info("unhandled message:{}", jo.toStringPretty());
                        break;
                }
            }

            return 0;
        }

        log.info("handle userData:{}", jo.toStringPretty());

        JSONObject joData = jo.getJSONObject("data");
        if (joData == null) {
            log.error("shouldn't be here, jo:{}", jo.toStringPretty());
            return 0;
        }

        String     event        = joData.getStr("e");
        Long       tsEvent      = joData.getLong("E");
        Long       tsMatch      = joData.getLong("T");
        JSONObject eventAccount = joData.getJSONObject("a");    // 账户更新事件

        switch (event) {
            // 余额及持仓变化
            case "outboundAccountPosition":
                handleUserEventAccountUpdate(hashMapBalance2, hashMapPosition, joData, tsMatch);

                log.info("listOrderUpdate queue size2:{}", listOrderUpdate.size());
                for (int i = 0; i < listOrderUpdate.size(); i++) {
                    jo = listOrderUpdate.removeFirst();
                    if (jo == null) {
                        break;
                    }

                    log.info("listOrderUpdate queue, handle idx:{}, left:{}, orderUpdate:{}", i, listOrderUpdate.size(), jo.toStringPretty());
                    handleUserEventOrderUpdate(clientAccountHTTP, jo);
                }

                break;
            // 订单变化
            // case "ORDER_TRADE_UPDATE":
            case "executionReport":
                // handleUserEventOrderUpdate(clientAccountHTTP, joData);

                String orderStatus = joData.getStr("X");
                if (orderStatus != null && orderStatus.equalsIgnoreCase("FILLED")) {
                    listOrderUpdate.addLast(joData);
                    log.info("listOrderUpdate queue addNew, total:{}", listOrderUpdate.size());
                }

                // 临时保存，通过
                break;
            // 账户配置变化，如杠杆倍数
            case "ACCOUNT_CONFIG_UPDATE":
                handleUserEventAccountConfigUpdate(joData);
                break;
            default:
                log.info("unhandled message:{}", jo.toStringPretty());
                break;
        }

        return 0;
    }

    private int handleMarket24HrMiniTicker(JSONObject jo) {
        BinanceMiniTicker ticker = BinanceMiniTicker.parseFromWebsSocketData(jo);
        mapMiniTicker.put(ticker.getSymbol(), ticker);

        if (ticker.getSymbol().equals(config.getSymbol())) {
            if (countIdMiniTicker.incrementAndGet() % 30 == 0) {
                log.info("symbol:{}, price:{}, ts:{}", ticker.getSymbol(), ticker.getPriceClose(), ticker.getEventTime());
            }
        }
        return 0;
    }

    private int handleUserEventAccountConfigUpdate(JSONObject jo) {
        log.info("AccountConfigEvent:{}", jo.toStringPretty());
        return 0;
    }

    private int handleUserEventAccountUpdate(ConcurrentHashMap<String, BinanceBalanceSpot> hashMapBalance2, ConcurrentHashMap<String, BinancePosition> hashMapPosition, JSONObject joEvent, Long ts) {
        JSONArray  ja = null;
        JSONObject jo = null;

        log.info("AccountUpdate ts:{}, event:{}", System.currentTimeMillis(), joEvent.toStringPretty());

        // 订单触发的 余额变动信息
        ja = joEvent.getJSONArray("B");     // 余额信息
        if (ja != null && !ja.isEmpty()) {
            BinanceBalanceSpot b = null;

            for (int i = 0; i < ja.size(); ++i) {
                jo = ja.getJSONObject(i);

                b = new BinanceBalanceSpot(
                        jo.getStr("a"),
                        jo.getBigDecimal("f"),
                        jo.getBigDecimal("l"),
                        ts
                );

                hashMapBalance2.put(b.getAsset(), b);

                BigDecimal total = b.getFree().add(b.getLocked());
                if (total.compareTo(BigDecimal.ZERO) > 0) {
                    log.info("BalanceUpdate, coin:{}, balance:{}/{}", b.getAsset(), b.getFree(), b.getLocked());
                }
            }
        }
        return 0;
    }

    // 现在时单向持仓，所以只有buy/sell
    private int handleUserEventOrderUpdate(
            SpotClient clientAccountHTTP,
            JSONObject jo
    ) {

        int        ret           = 0;
        String     symbol        = jo.getStr("s");
        String     orderId       = jo.getStr("i");
        String     orderIdLink   = jo.getStr("c");
        String     side          = jo.getStr("S");
        BigDecimal priceAvg      = jo.getBigDecimal("p");  // 实际成交价 - 现货没有average prive
        BigDecimal priceOriginal = jo.getBigDecimal("p");  // 委托价格
        String     orderStatus   = jo.getStr("X");
        BigDecimal amountFilled  = jo.getBigDecimal("z");  // 订单累计已成交量
        BigDecimal amountTotal   = jo.getBigDecimal("q");  // 订单原始数量
        String     orderType     = jo.getStr("o");         // 订单类型
        Long       updateTime    = jo.getLong("W");         // 订单类型

        String assetQuote;
        String assetBase;

        switch (symbol) {
            case "USDCUSDT":
                assetBase = "USDC";
                assetQuote = "USDT";
                break;
            case "FDUSDUSDT":
                assetBase = "FDUSD";
                assetQuote = "USDT";
                break;
            default:
                log.error("2000 OrderUpdate unsupported symbol:{}", symbol);
                return -1;
        }

        int        leverageMaxConfig        = config.getLeverage();
        int        leverageMaxConfigAllowed = config.getLeverage();
        BigDecimal amountInit               = config.getPositionInit();
        BigDecimal amountHoldMax            = config.getPositionMax();
        BigDecimal amountHoldMin            = config.getPositionMin();
        BigDecimal amountOrderOpen          = config.getAmountOpen();
        BigDecimal amountOrderClose         = config.getAmountClose();
        BigDecimal percentOpen              = config.getPercentOpen();
        BigDecimal percentClose             = config.getPercentClose();
        int        configId                 = config.getId();
        int        priceScale               = symbolConfig.getTickSize();   // 小数位数

        // 生成订单号，替代手动订单的订单号
        String  orderIdConnect = null;
        boolean manualOrder    = false;

        // configId, ts, seqId, "b"
        // configId, ts, seqId, "s"
        // configId, ts, seqId, "stop"

        String[] split = orderIdLink.split("-");
        if (split.length != 4) {
            manualOrder = true;
            // 手动单处理逻辑

            // 生成新的订单ID，用来关联手动订单
            HashMap<String, String> hashMapOrderId = HelperOrder.generateOrderIdLink_Normal(configId, 0);
            switch (side.toUpperCase()) {
                case "SELL":
                    orderIdConnect = hashMapOrderId.get("orderIdSell");
                    break;
                case "BUY":
                    orderIdConnect = hashMapOrderId.get("orderIdBuy");
                    break;
                default:
                    log.error("OrderUpdate, unsupported order side:{}", jo.toStringPretty());
                    break;
            }

            split = orderIdConnect.split("-");
            if (split.length != 4) {
                log.info("OrderUpdate connect failed, orderId:{}, orderIdLink:{}, orderIdConnect:{}, order:{}", orderId, orderIdLink, orderIdConnect, jo.toStringPretty());
                return -1;
            }

            log.info("OrderUpdate connect orderId,        orderId:{}", orderId);
            log.info("OrderUpdate connect orderId,    orderIdLink:{}", orderIdLink);
            log.info("OrderUpdate connect orderId, orderIdConnect:{}", orderIdConnect);
            log.info("OrderUpdate connect orderId,    orderIdLink:{} -> orderIdConnect:{}", orderIdLink, orderIdConnect);
            // 填入临时生成的orderIdLink
            orderIdLink = orderIdConnect;
        }

        if (!split[0].equalsIgnoreCase(config.getId() + "")) {
            log.info("OrderUpdate ignore unrelated orderId:{}, configId:{}", orderIdLink, config.getId());
            return 0;
        }

        //        switch (split[0]) {
        //            case Constants.orderPrefix:
        //            case Constants.orderPrefixInitBuy:
        //                break;
        //            default:
        //                log.info("OrderUpdate ignore unrelated order:{}", orderIdLink);
        //                break;
        //        }

        // 订单号格式: configId-timestamp-seqId-b/s/stop
        String configIdTmp = split[0];  // configId
        String ts          = split[1];  // 下单的timestamp
        String seqString   = split[2];  // 下单批次号
        String bs          = split[3];  // busy/sell

        int seqId          = Integer.parseInt(seqString);
        int configIdFromDB = Integer.parseInt(configIdTmp);

        // 丢弃 非当前任务的订单
        if (configIdFromDB != configId) {
            log.info("OrderUpdate drop status:{}, type:{}, order:{}", orderStatus, orderType, jo);
            return 0;
        }

        switch (orderStatus) {
            case "FILLED":
                log.info("OrderUpdate process status:{}, type:{}, order:{}", orderStatus, orderType, jo);
                break;
            case "NEW":
            case "PARTIALLY_FILLED":
            case "CANCELED":
            case "EXPIRED":
            default:
                log.info("OrderUpdate ignore status:{}, type:{}, order:{}", orderStatus, orderType, jo);
                return 0;
        }

        {
            boolean updateRet = false;
            try {
                updateRet = new LambdaUpdateChainWrapper<>(configMapper)
                        .eq(TConfig::getId, config.getId())
                        .set(TConfig::getPriceLastMatch, priceAvg)
                        .update();

            } catch (Exception e) {
                log.error("TaskStatusUpdate exception:", e);
                e.printStackTrace();
            }

            log.info("OrderUpdate priceLastMatch orderId:{}/{}, price:{}, orderType:{}, updateRet:{}", orderId, orderIdLink, priceAvg, orderType, updateRet);
        }

        // 对于成交的订单通知，只能处理一次
        {
            String  key   = "orderIdHandled:" + orderId;
            Long    value = System.currentTimeMillis();
            Boolean b     = false;

            try {
                b = redisClient.setIfAbsent(key, value + "", 10, TimeUnit.MINUTES);

                if (!b) {
                    log.error("setIfAbsent ignore duplicated order:{}/{}", orderId, orderIdLink);
                    larkService.SendLarkDuplicateOrder(config, symbol, orderId, orderIdLink, priceAvg, amountFilled, updateTime);
                    return 0;
                }
            } catch (Exception e) {
                log.info("setIfAbsent setIfAbsent failed, key:{}, value:{}", key, value);
            }
        }

        // 发送订单成交通知
        larkService.SendLarKOrderFilled(config, symbol, priceAvg, amountFilled, updateTime, BigDecimal.ZERO, manualOrder, side);

        // 撤销所有订单, 不管当前是否有订单
        // 这个方法不行，之前设置的止损，止盈订单也同样被撤销
        ret = QuantUtil.orderCancelAll(clientAccountHTTP, symbol);
        if (ret != 0) {
            log.error("OrderUpdate orderCancelAll failed, symbol:{}", symbol);
            return -1;
        } else {
            log.info("OrderUpdate orderCancelAll done, symbol:{}", symbol);
            QuantUtil.waitMs(10);
        }

        boolean canOpen  = false;
        boolean canClose = false;
        int     ret1     = 0;
        int     ret2     = 0;

        BigDecimal priceClose        = null;
        BigDecimal priceOpen         = null;
        BigDecimal balanceAssetBase  = BigDecimal.ZERO;
        BigDecimal balanceAssetQuote = BigDecimal.ZERO;

        // 撤销完成，继续下单
        // 初始化 余额 & 持仓
        BinanceBalanceSpot spotBalanceBase = hashMapBalance.get(assetBase);
        if (spotBalanceBase != null) {
            balanceAssetBase = spotBalanceBase.getFree();
        }

        BinanceBalanceSpot spotBalanceQuote = hashMapBalance.get(assetQuote);
        if (spotBalanceQuote != null) {
            balanceAssetQuote = spotBalanceQuote.getFree();
        }

        // 持续买入，不管当前余额是否足够

        // 补充买入，没有明确要求，保持原来的逻辑
        BigDecimal amountOpen  = amountHoldMax.subtract(balanceAssetBase).min(amountOrderOpen);
        BigDecimal amountClose = balanceAssetBase.subtract(amountHoldMin).min(amountOrderClose);

        Integer quantityScale = symbolConfig.getQuantityPrecise();
        if (quantityScale != null && quantityScale >= 0) {
            amountOpen  = amountOpen.setScale(quantityScale, RoundingMode.FLOOR);
            amountClose = amountClose.setScale(quantityScale, RoundingMode.FLOOR);
        }

        if (amountOpen.compareTo(BigDecimal.ZERO) > 0) {
            canOpen = true;
        } else {
            amountOpen = BigDecimal.ZERO;
            larkService.SendLarkReachMax(config, symbol, priceAvg, amountFilled, updateTime, balanceAssetQuote);
        }

        if (amountClose.compareTo(BigDecimal.ZERO) > 0) {
            canClose = true;
        } else {
            amountClose = BigDecimal.ZERO;
            larkService.SendLarkReachMin(config, symbol, priceAvg, amountFilled, updateTime, balanceAssetBase);
        }

        log.info("2000 OrderUpdate  canOpen:{}, balance:{}/{}, Max:{},  amountOpen:{}, amountConfig:{}", canOpen, balanceAssetBase, balanceAssetQuote, amountHoldMax.stripTrailingZeros().toPlainString(), amountOpen, amountOrderOpen);
        log.info("2000 OrderUpdate canClose:{}, balance:{}/{}, Min:{}, amountClose:{}, amountConfig:{}", canClose, balanceAssetBase, balanceAssetQuote, amountHoldMin.stripTrailingZeros().toPlainString(), amountClose, amountOrderClose);

        // 以最后一次成交的价格为基础，上浮平仓，下降开仓
        // NOTICE: 不能 > 最大值持仓量
        // NOTICE: 不能 < 最小持仓量
        // TODO: 需要使用系统提供的价格精度
        int newScale = priceScale;
        log.info("OrderUpdate price scale {}:{}", symbol, newScale);

        priceOpen  = priceAvg.subtract(percentOpen).setScale(newScale, RoundingMode.HALF_DOWN).stripTrailingZeros();
        priceClose = priceAvg.add(percentClose).setScale(newScale, RoundingMode.HALF_UP).stripTrailingZeros();
        priceAvg   = priceAvg.setScale(newScale, RoundingMode.HALF_UP).stripTrailingZeros();

        // 生成新的订单ID
        HashMap<String, String> hashMapOrderId  = HelperOrder.generateOrderIdLink_Normal(configId, seqId + 1);
        String                  orderIdNewOpen  = hashMapOrderId.get("orderIdBuy");
        String                  orderIdNewClose = hashMapOrderId.get("orderIdSell");

        log.info("");
        log.info("2000 OrderUpdate prepare order  BUY id:{}, price:{}, percent:{},  canOpen:{}, scale:{}", orderIdNewOpen, priceOpen, percentOpen.toPlainString(), canOpen, newScale);
        log.info("2000 OrderUpdate prepare order SELL id:{}, price:{}, percent:{}, canClose:{}, scale:{}", orderIdNewClose, priceClose, percentClose.toPlainString(), canClose, newScale);
        log.info("");

        if (canClose) {
            ret1 = QuantUtil.placeOrderSell(clientAccountHTTP, orderIdNewClose, symbol, amountClose, priceClose);
            switch (ret1) {
                case 0:
                    log.info("2000 new close-0: id:{}->{}, price:{} -> {}, amt:{}, change:{}, ret:{} success", orderIdLink, orderIdNewClose, priceAvg, priceClose, amountClose, percentClose, ret1);
                    break;
                case -2010:
                    log.info("2000 new close-0: id:{}->{}, price:{} -> {}, amt:{}, change:{}, ret:{}, balanceInsufficient", orderIdLink, orderIdNewClose, priceAvg, priceClose, amountClose, percentClose, ret1);
                    larkService.sendLarkBalanceInsufficient(config, symbol, priceAvg, amountFilled, updateTime, assetBase, balanceAssetBase, "SELL");
                    break;
                default:
                    log.info("2000 new close-0: id:{}->{}, price:{} -> {}, amt:{}, change:{}, ret:{}, failed", orderIdLink, orderIdNewClose, priceAvg, priceClose, amountClose, percentClose, ret1);
                    break;
            }

        } else {
            log.info("2000 new close-1: id:{}->{}, price:{} -> {}, amt:{}, change:{}, reachMin", orderIdLink, orderIdNewClose, priceAvg, priceClose, amountClose, percentClose);
        }

        if (canOpen) {
            ret2 = QuantUtil.placeOrderBuy(clientAccountHTTP, orderIdNewOpen, symbol, amountOpen, priceOpen);

            switch (ret2) {
                case 0:
                    log.info("2000 new close-0: id:{}->{}, price:{} -> {}, amt:{}, change:{}, ret:{} success", orderIdLink, orderIdNewOpen, priceAvg, priceClose, amountClose, percentClose, ret1);
                    break;
                case -2010:
                    log.info("2000 new  open-0: id:{}->{}, price:{} -> {}, amt:{}, change:{}, ret:{}, balanceInsufficient", orderIdLink, orderIdNewOpen, priceAvg, priceOpen, amountOpen, percentOpen, ret2);
                    larkService.sendLarkBalanceInsufficient(config, symbol, priceAvg, amountFilled, updateTime, assetBase, balanceAssetBase, "SELL");
                    break;
                default:
                    log.info("2000 new close-0: id:{}->{}, price:{} -> {}, amt:{}, change:{}, ret:{}, failed", orderIdLink, orderIdNewOpen, priceAvg, priceClose, amountClose, percentClose, ret1);
                    break;
            }
        } else {
            log.info("2000 new  open-1: id:{}->{}, price:{} -> {}, amt:{}, change:{}, reachMax", orderIdLink, orderIdNewOpen, priceAvg, priceOpen, amountOpen, percentOpen);
        }

        log.info("");
        return 0;
    }

    public TConfig configGet() {
        return config;
    }

    public TSymbolConfig symbolConfigGet() {
        return symbolConfig;
    }

    public TSymbolConfig symbolConfigSet(TSymbolConfig symbolConfig) {
        this.symbolConfig = symbolConfig;
        return this.symbolConfig;
    }

    public void checkWebSocketStatus() {
        BinanceMiniTicker ticker = null;

        TConfig config = this.config;
        if (config == null) {
            log.error("checkWebSocketStatus exit, config is null");
            return;
        }

        if (config.getDeleted() != null && config.getDeleted() == 1) {
            log.error("checkWebSocketStatus exit, config is deleted");
            return;
        }

        String symbol = config.getSymbol();
        if (symbol == null) {
            log.error("checkWebSocketStatus exit, symbol is invalid");
            return;
        }

        ticker = mapMiniTicker.get(symbol);
        if (ticker == null) {
            log.error("checkWebSocketStatus exit, no data, symbol:{}", symbol);
            return;
        }

        long tsNow  = System.currentTimeMillis();
        long tsLast = ticker.getEventTime();
        long tsGap  = tsNow - tsLast;

        if (tsGap > 30 * 1000) {
            log.warn("checkWebSocketStatus send warning, symbol:{}, tsGap:{}", symbol, tsGap);
            larkService.SendWebSocketErrorEvent(config, config.getSymbol());
        }
    }
}

