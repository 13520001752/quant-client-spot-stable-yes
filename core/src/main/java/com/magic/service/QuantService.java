//package com.magic.service;
//
//import cn.hutool.json.JSONObject;
//import com.binance.connector.futures.client.impl.UMFuturesClientImpl;
//import com.binance.connector.futures.client.impl.UMWebsocketClientImpl;
//import com.magic.configuration.BinancePriceTickSize;
//import com.magic.constant.Constants;
//import com.magic.emum.BizErrorEnum;
//import com.magic.mybatisplus.entity.TOrder;
//import com.magic.mybatisplus.service.TOrderService;
//import com.magic.vo.daemon.AveragePriceGetResponse;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.tuple.Pair;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.math.RoundingMode;
//import java.util.*;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.Semaphore;
//
//@Slf4j
//@Service
//public class QuantService {
//    @Autowired
//    private ApplicationArguments     arguments;
////    @Autowired
////    private HelperBinanceUserEvent   helperBinanceUserEvent;
//    @Autowired
//    private HelperBinanceAccountInfo helperBinanceAccountInfo;
//    @Autowired
//    private HelperBinanceMarketPrice helperBinanceMarketPrice;
//    @Autowired
//    private HelperBinanceStopOrder   helperBinanceStopOrder;
//    @Autowired
//    private BinancePriceTickSize     binancePriceTickSize;
//    @Autowired
//    private TOrderService            orderService;
//    @Autowired
//    private HelperDaemon             helperDaemon;
//
//    // 100.myquant@gmail.com
//    // 1-100-myquant
//    // private String apiKey    = "BQPPirmuZHFuMWue3etlTzsHGuVqfiyvi3hsi3PMgYf3Ue17v729jA4ssg78sgrJ";
//    // private String apiSecret = "dRpCKyjKSWsr50M0ZutjtGRWlCfEwpBTclgF3bSPSLICUtyvWeiFihgXGEykWjje";
//
//    // 1000u eth
//    // private String apiKey    = "rVhIFNFiOPrXiuiZpst8JaT55AENbES8vCTlN2qfWNUwtM4ME8FKTOIF0Lkd6sIv";
//    // private String apiSecret = "ATqM1Zk8MdIh4Vqj841cAJacDu33tNbfwoZLHszstdEBdALTjH1VCkWwciehDOFY";
//
//    //    private String apiKey    = "3fzsWzhtWax4W2N6Tzr0OzQ9t7AVNvrNWgcFDM6OZRssUGMfzk2KdYDNL2yxURUq";
//    //    private String apiSecret = "ioDn0lINnwinsx8je8YgVYL3N71IKk7nF78mkOfzbE43upo78sTuVHr513JDgcWP";
//
//    // ------------------
//    // symbol, BinancePriceMark
//    public ConcurrentHashMap<String, BinancePriceMark> hashMapPrice    = new ConcurrentHashMap<>();
//    public ConcurrentHashMap<String, BinancePosition>  hashMapPosition = new ConcurrentHashMap<>();
//    public ConcurrentHashMap<String, BinanceBalance>   hashMapBalance  = new ConcurrentHashMap<>();
//    public ConcurrentHashMap<String, BinanceBalance2>  hashMapBalance2 = new ConcurrentHashMap<>();
//
//    // symbol, price: 量化订单最新成交价
//    public ConcurrentHashMap<String, BigDecimal> hashTradePriceLast = new ConcurrentHashMap<>();
//
//    // symbol, 持仓价值
//    ConcurrentHashMap<String, BigDecimal> hashMapPositionValue = new ConcurrentHashMap<>();
//
//    // ------------------
//    public UMFuturesClientImpl   clientHTTP        = null;
//    public UMWebsocketClientImpl clientWebSocket   = null;
//    public UMFuturesClientImpl   clientAccountHTTP = null;
//
//    public volatile Thread  threadListenMonitor;
//    public volatile String  ListenKey;
//    public volatile Boolean ListenKeyFirstTime = true;
//    public volatile int     listenKeyStreamId  = -1;
//    public volatile String  listenStatus       = Constants.LISTEN_STATUS_STOPPED;
//
//    private volatile boolean initDone = false;
//    private volatile boolean doExit   = false;
//
//    @Value("${quant-net1.apiKey}")
//    private String apiKey;
//
//    @Value("${quant-net1.apiSecret}")
//    private String apiSecret;
//
//    @Value("${quant-net1.symbol}")
//    public String symbol;
//
//    @Value("${quant-net1.leverageMaxConfig}")
//    public int leverageMaxConfig;
//    @Value("${quant-net1.leverageMaxAllowed}")
//    public int leverageMaxAllowed;
//
//    // 初始买入数量
//    @Value("${quant-net1.amountInit}")
//    public volatile BigDecimal amountInit;
//
//    @Value("${quant-net1.amountHoldMax}")
//    public volatile BigDecimal amountHoldMax;
//    @Value("${quant-net1.amountHoldMin}")
//    public volatile BigDecimal amountHoldMin;
//
//    @Value("${quant-net1.amountOrderOpen}")
//    public volatile BigDecimal amountOrderOpen;
//    @Value("${quant-net1.amountOrderClose}")
//    public volatile BigDecimal amountOrderClose;
//
//    @Value("${quant-net1.percentOpen}")
//    public volatile BigDecimal percentOpen;
//    @Value("${quant-net1.percentClose}")
//    public volatile BigDecimal percentClose;
//    @Value("${quant-net1.percentStop}")
//    public volatile BigDecimal percentStop;
//
//    private Semaphore semaphoreFlowLock = new Semaphore(0);
//
//    public volatile int scalePrice = 1;
//
//    public int start() {
//        JSONObject jo   = null;
//        String     resp = null;
//        int        code = 0;
//
//        clientHTTP        = new UMFuturesClientImpl();
//        clientWebSocket   = new UMWebsocketClientImpl();
//        clientAccountHTTP = new UMFuturesClientImpl(apiKey, apiSecret);
//
//        //        String s = clientHTTP.market().exchangeInfo();
//        //        log.info("s:{}", s);
//
//        log.info("");
//        log.info("parameters");
//        log.info("---------------------------");
//        log.info("                 Symbol:{}", symbol);
//        log.info("      leverageMaxConfig:{}", leverageMaxConfig);
//        log.info("     leverageMaxAllowed:{}", leverageMaxAllowed);
//
//        log.info("            amountHoldInit:{}", amountInit);
//        log.info("             amountHoldMax:{}", amountHoldMax);
//        log.info("             amountHoldMin:{}", amountHoldMin);
//
//        log.info("         amountOrderOpen:{}", amountOrderOpen);
//        log.info("        amountOrderClose:{}", amountOrderClose);
//
//        log.info("             percentOpen:{}", percentOpen);
//        log.info("            percentClose:{}", percentClose);
//        log.info("             percentStop:{}", percentStop);
//
//        log.info("                 apiKey:{}", apiKey);
//        log.info("              apiSecret:{}", apiSecret.substring(0, 4));
//        log.info("---------------------------");
//        log.info("");
//
//        if (percentStop.compareTo(BigDecimal.ZERO) != 1) {
//            log.error("configuration error: bad percentStop:{}", percentStop);
//            System.exit(-1);
//        }
//
//        scalePrice = binancePriceTickSize.getPriceScale(symbol);
//        if (scalePrice < 1 || scalePrice > 7) {
//            log.info("Quant start failed, bad price precision, symbol:{}", symbol);
//            System.exit(-2);
//        } else {
//            log.info("price scale, {}:{}", symbol, scalePrice);
//            helperBinanceUserEvent.priceScale = scalePrice;
//            helperBinanceStopOrder.priceScale = scalePrice;
//        }
//
//        // 生成订单号
//        HashMap<String, String> mapTmp         = HelperOrder.generateOrderIdLink_Normal(1);
//        String                  orderIdNewBuy  = mapTmp.get("orderIdBuy");
//        String                  orderIdNewSell = mapTmp.get("orderIdSell");
//
//        // TODO: 参数校验
//        JSONObject joParam = new JSONObject();
//        joParam.putOnce("symbol", symbol);
//        joParam.putOnce("leverageMaxConfig", leverageMaxConfig);
//        joParam.putOnce("leverageMaxAllowed", leverageMaxAllowed);
//        joParam.putOnce("amountHoldInit", amountInit);
//        joParam.putOnce("amountHoldMax", amountHoldMax);
//        joParam.putOnce("amountHoldMin", amountHoldMin);
//        joParam.putOnce("amountOrderOpen", amountOrderOpen);
//        joParam.putOnce("amountOrderClose", amountOrderClose);
//        joParam.putOnce("percentOpen", percentOpen);
//        joParam.putOnce("percentClose", percentClose);
//        joParam.putOnce("apiKey", apiKey);
//        joParam.putOnce("apiSecret", apiSecret.substring(0, 4));
//
//        if (percentClose.compareTo(BigDecimal.ZERO) != 1
//                || percentOpen.compareTo(BigDecimal.ZERO) != 1
//                || amountInit.compareTo(BigDecimal.ZERO) != 1
//                || amountHoldMax.compareTo(BigDecimal.ZERO) != 1
//                // || amountHoldMin.compareTo(BigDecimal.ZERO) != 1
//                || amountOrderOpen.compareTo(BigDecimal.ZERO) != 1
//                || amountOrderClose.compareTo(BigDecimal.ZERO) != 1
//        ) {
//            log.info("bad parameter:{}", joParam.toStringPretty());
//            return -1;
//        }
//
//        // 设置单向持仓模式
//        // 当前模式下，双向持仓应该最安全，不会产生空单
//        Boolean isDualMode = QuantUtil.positionSideGet(clientAccountHTTP);
//        if (isDualMode) {
//            QuantUtil.positionSideSetToSingle(clientAccountHTTP);
//        }
//        log.info("preCheck positionSideIsDualMode:{}", isDualMode);
//
//        // 杠杆设置
//        int ret = QuantUtil.leverageSet(clientAccountHTTP, symbol, leverageMaxConfig);
//        if (ret != 0) {
//            log.error("preCheck, leverageSet failed, system exit");
//            System.exit(-1);
//            return -1;
//        }
//        QuantUtil.waitMs(100);
//
//        // 订阅持仓 & 余额变化
//        threadListenMonitor = new Thread(() -> {
//            try {
//                threadListenMonitor();
//            } catch (Exception e) {
//                log.error("threadListenMonitor exception:", e);
//                System.exit(-1);
//            }
//        });
//        threadListenMonitor.start();
//
//        try {
//            semaphoreFlowLock.acquire(1);
//        } catch (Exception e) {
//            log.error("threadListenMonitor wait exception:{}", e.getMessage());
//            e.printStackTrace();
//            return -1;
//        }
//        log.info("threadListenMonitor is ready, continue");
//
//        // 订阅价格
//        //        final int acceptedSpeed = 1;
//        //        clientWebSocket.allMarkPriceStream(acceptedSpeed, (msg) -> {
//        //            helperBinanceMarketPrice.handle(hashMapPrice, hashMapPosition, msg);
//        //        });
//
//        // 账户操作
//        // 撤销所有订单
//        //        ret = QuantUtil.orderCancelAll(clientAccountHTTP, symbol);
//        //        if (ret != 0) {
//        //            log.info("mainInit, cancel all failed, symbol:{}, ret:{}", symbol, ret);
//        //            System.exit(-4);
//        //        } else {
//        //            log.info("mainInit, cancel all success, symbol:{}, ret:{}, sleep 300ms", symbol, ret);
//        //            QuantUtil.waitMs(300);
//        //        }
//        //////////////////////////////////////////////////////////////////////////
//
//        // 订单数据同步
//        BizErrorEnum codeBiz        = BizErrorEnum.SUCCESS;
//        BigDecimal   priceLastMatch = null;
//
//        TOrder orderTmp       = null;
//        TOrder orderCancelled = null;
//
//        Pair<Integer, List<TOrder>> retGetOrderOpen    = null;
//        Pair<Integer, List<TOrder>> retGetOrderHistory = null;
//        Pair<BizErrorEnum, TOrder>  retCancelOrder     = null;
//
//        List<TOrder> listOrderOpen      = null;
//        List<TOrder> listOrderHistory   = null;
//        List<TOrder> listOrderNotFinish = null;
//
//        HashSet<String> hashSetOrderIdToQuery = new HashSet<>();
//
//        // 订单同步1:查询所有Open的订单
//        retGetOrderOpen = QuantUtil.getOpenOrderBySymbol(clientAccountHTTP, symbol);
//        code            = retGetOrderOpen.getKey();
//        listOrderOpen   = retGetOrderOpen.getValue();
//        if (code != 0) {
//            log.info("mainInit OpenOrderGet failed, symbol:{}, system exit", symbol);
//            System.exit(-1);
//            return -1;
//        } else {
//            log.info("mainInit OpenOrderGet Num:{}", listOrderOpen == null ? 0 : listOrderOpen.size());
//
//            if (listOrderOpen != null && !listOrderOpen.isEmpty()) {
//
//                // 逐个撤销订单 & DB更新
//                for (int i = 0; i < listOrderOpen.size(); ++i) {
//                    orderTmp = listOrderOpen.get(i);
//
//                    retCancelOrder = QuantUtil.orderCancelByOrderId2(clientAccountHTTP, symbol, orderTmp.getOrderId());
//                    codeBiz        = retCancelOrder.getKey();
//                    orderCancelled = retCancelOrder.getValue();
//
//                    if (codeBiz.equals(BizErrorEnum.SUCCESS)) {
//                        ret = orderService.doUpdate(orderCancelled);
//                        log.info("mainInit OpenOrderCancel idx:{}/{}, ret:{}, order:{}", i, listOrderOpen.size(), ret, HelperOrder.getBriefInfo(orderTmp));
//                    } else {
//                        hashSetOrderIdToQuery.add(orderTmp.getOrderId());
//                        log.error("mainInit OpenOrderCancel failed, idx:{}/{}, ret:{}, order:{}", i, listOrderOpen.size(), ret, HelperOrder.getBriefInfo(orderTmp));
//                    }
//                    QuantUtil.waitMs(300);
//                }
//            }
//        }
//
//        // 账户操作
//        // 撤销所有订单
//        //        ret = QuantUtil.orderCancelAll(clientAccountHTTP, symbol);
//        //        if (ret != 0) {
//        //            log.info("mainInit, cancel all failed, symbol:{}, ret:{}", symbol, ret);
//        //            System.exit(-4);
//        //        } else {
//        //            log.info("mainInit, cancel all success, symbol:{}, ret:{}, sleep 300ms", symbol, ret);
//        //            QuantUtil.waitMs(300);
//        //        }
//
//
//        // 订单同步2:查询所有历史订单
//        retGetOrderHistory = QuantUtil.queryHistoryOrderFromHttp(clientAccountHTTP, symbol);
//        code               = retGetOrderHistory.getKey();
//        listOrderHistory   = retGetOrderHistory.getValue();
//
//        if (code == 0) {
//            if (listOrderHistory != null && !listOrderHistory.isEmpty()) {
//                ret = orderService.doUpdate(listOrderHistory);
//                log.info("mainInit historyOrderGet HistoryOrderNum:{}, doUpdate:{}", listOrderHistory.size(), ret);
//            } else {
//                log.info("mainInit historyOrderGet HistoryOrderNum:{}", 0);
//            }
//            QuantUtil.waitMs(500);
//        } else {
//            log.info("mainInit historyOrderGet failed");
//            System.exit(-1);
//        }
//
//        // 订单同步3:查询DB未完成订单
//        listOrderNotFinish = orderService.getNotFinished(Constants.ExchangeBinance, clientAccountHTTP.getApiKey(), symbol);
//
//        if (listOrderNotFinish != null && !listOrderNotFinish.isEmpty()) {
//
//            listOrderNotFinish.forEach(order -> {
//                hashSetOrderIdToQuery.add(order.getOrderId());
//                log.info("mainInit dbOrderNotFinishGet, order:{}", HelperOrder.getBriefInfo(order).toStringPretty());
//            });
//        } else {
//            log.info("mainInit dbOrderNotFinishGet orderNotFinishNum:0");
//        }
//
//        // 订单同步4: 查询订单 & 更新
//        log.info("mainInit doubleCheck , hashSetOrderIdToQuery:{}", hashSetOrderIdToQuery.size());
//        for (String orderId : hashSetOrderIdToQuery) {
//            Pair<BizErrorEnum, TOrder> retGetOrder = QuantUtil.getOrderByOrderId(clientAccountHTTP, symbol, orderId);
//            codeBiz  = retGetOrder.getKey();
//            orderTmp = retGetOrder.getValue();
//
//            if (!codeBiz.equals(BizErrorEnum.SUCCESS)) {
//                // TODO: 异常处理
//                log.error("mainInit doubleCheck failed, orderId:{}, ret:{}", orderId, retGetOrder.getKey());
//            } else {
//                ret = orderService.doUpdate(orderTmp);
//                log.info("mainInit doubleCheck success, doUpdate ret:{}, order:{}", ret, HelperOrder.getBriefInfo(orderTmp).toStringPretty());
//            }
//            QuantUtil.waitMs(200);
//        }
//
//        // DB查询最后一笔成交订单
//        TOrder lastFilledOrder         = null;
//        TOrder lastFilledOrderFromHttp = QuantUtil.findLastMatchedOrder(listOrderHistory);
//        TOrder lastFilledOrderFromDB   = orderService.getLastFilledOrder(clientAccountHTTP.getApiKey(), Constants.ExchangeBinance, symbol);
//
//        if (lastFilledOrderFromHttp != null && lastFilledOrderFromDB != null) {
//            log.info("mainInit lastFilledOrderFromHttp order:{}", HelperOrder.getBriefInfo(lastFilledOrderFromHttp).toStringPretty());
//            log.info("mainInit lastFilledOrderFromDB   order:{}", HelperOrder.getBriefInfo(lastFilledOrderFromDB).toStringPretty());
//
//            if (lastFilledOrderFromHttp.getOrderTimeUpdate() > lastFilledOrderFromDB.getOrderTimeUpdate()) {
//                lastFilledOrder = lastFilledOrderFromHttp;
//                log.info("mainInit lastFilledOrderFromHttp isLatest1, orderId:{}", HelperOrder.getBriefInfo(lastFilledOrderFromHttp).toStringPretty());
//            } else {
//                lastFilledOrder = lastFilledOrderFromDB;
//                log.info("mainInit lastFilledOrderFromDB   isLatest2, orderId:{}", HelperOrder.getBriefInfo(lastFilledOrderFromDB).toStringPretty());
//            }
//        } else {
//            if (lastFilledOrderFromHttp != null) {
//                lastFilledOrder = lastFilledOrderFromHttp;
//                log.info("mainInit lastFilledOrderFromHttp isLatest3, order:{}", HelperOrder.getBriefInfo(lastFilledOrder).toStringPretty());
//            }
//
//            if (lastFilledOrderFromDB != null) {
//                lastFilledOrder = lastFilledOrderFromDB;
//                log.info("mainInit lastFilledOrderFromDB   isLatest4, order:{}", HelperOrder.getBriefInfo(lastFilledOrder).toStringPretty());
//            }
//        }
//
//        // 最后一笔成交价: LIMIT/Market, 量化单, Filled
//        if (lastFilledOrder != null) {
//            priceLastMatch = lastFilledOrder.getOrderPriceAverage();
//            log.info("mainInit lastFilledOrderFromDB priceLastMatch:{}", priceLastMatch.toPlainString());
//        } else {
//            priceLastMatch = null;
//            log.info("mainInit lastFilledOrderFromDB priceLastMatch NotExist");
//        }
//
//        //        // 再次查询订单, 确保DB订单数据最新
//        //        if (listOrderOpen != null && !listOrderOpen.isEmpty()) {
//        //            log.info("mainInit OpenOrderGetInfo:{}", listOrderOpen.size());
//        //
//        //            for (int i = 0; i < listOrderOpen.size(); ++i) {
//        //                orderTmp = listOrderOpen.get(i);
//        //
//        //                retGetOrder = QuantUtil.orderInfoGet(clientAccountHTTP, symbol, orderTmp.getOrderId());
//        //                codeBiz     = retGetOrder.getKey();
//        //                orderGot    = retGetOrder.getValue();
//        //
//        //                if (codeBiz.getCode().intValue() != BizErrorEnum.SUCCESS.getCode().intValue()) {
//        //                    log.error("mainInit OpenOrderGetInfo failed, idx:{}/{}, orderId:{}/{}", i, listOrderOpen.size(), orderTmp.getOrderId(), orderTmp.getOrderIdLink());
//        //                } else {
//        //                    int aa = orderService.doUpdate(orderCancelled);
//        //                    log.info("mainInit OpenOrderGetInfo success, idx:{}/{}, orderId:{}/{}, doUpdate ret:{}", i, listOrderOpen.size(), orderTmp.getOrderType(), orderTmp.getOrderIdLink(), aa);
//        //                }
//        //            }
//        //        } else {
//        //            log.info("mainInit OpenOrderGetInfo:0");
//        //        }
//        //        QuantUtil.waitMs(500);
//
//        ////////////////////////////////////////////////////////////////////////
//
//        // 查询当前持仓
//        Pair<BigDecimal, BigDecimal> result          = helperBinanceAccountInfo.getPositionAndBalanceOnline(clientAccountHTTP, symbol, "USDT");
//        BigDecimal                   balanceCurrent  = result.getLeft();
//        BigDecimal                   positionCurrent = result.getRight();
//
//        log.warn("mainInit getBalanceAndPosition symbol:{}, balance:{}, position:{}", symbol, balanceCurrent, positionCurrent);
//
//        // 持仓 == 0, 市价买入初始化个
//        if (positionCurrent.compareTo(BigDecimal.ZERO) == 0) {
//            ret = QuantUtil.singleModeOrderBuy(clientAccountHTTP, symbol, orderIdNewBuy, amountInit, null);
//            log.info("mainInit, orderMarketBuy amountInit:{}, ret:{}", amountInit, ret);
//
//            // 这里不刷新，通过ACCOUNT UPDATE触发止损单，因为买入一定会触发ACCOUNT_UPDATE
//            // helperBinanceUserEvent.semaphoreLiqPriceUpdate.release();
//            initDone = true;
//            return 0;
//        }
//
//        // 获取市场当前价格
//        //        BigDecimal                priceMarket = null;
//        //        Pair<Integer, BigDecimal> r           = null;
//        //
//        //        r           = HelperBinanceMarketPrice.getMarketPrice(clientAccountHTTP, symbol);
//        //        code        = r.getKey();
//        //        priceMarket = r.getValue();
//        //        if (code != 0) {
//        //            log.info("mainInit, get marketPrice failed, symbol:{}", symbol);
//        //            priceMarket = null;
//        //        } else {
//        //            log.info("mainInit, get marketPrice success, symbol:{}, priceMarket:{}", symbol, priceMarket);
//        //        }
//
//        // 获取最后一笔成交价
//        //        Pair<Integer, BinanceOrderHttp> resultLastOrder = null;
//        //        BinanceOrderHttp                orderLatest     = null;
//        //        BigDecimal                      priceLastMatch  = null;
//        //
//        //        resultLastOrder = QuantUtil.queryLastMatchedOrder(clientAccountHTTP, symbol);
//        //        code            = resultLastOrder.getKey();
//        //        orderLatest     = resultLastOrder.getValue();
//        //
//        //        if (code == 0 && orderLatest != null && orderLatest.getAveragePrice().compareTo(BigDecimal.ZERO) == 1) {
//        //            priceLastMatch = orderLatest.getAveragePrice();
//        //            log.info("mainInit, queryLastMatchedOrder success, symbol:{}, order:{}", symbol, new JSONObject(resultLastOrder).toStringPretty());
//        //        } else {
//        //            priceLastMatch = null;
//        //            log.error("mainInit, queryLastMatchedOrder failed, symbol:{}", symbol);
//        //        }
//
//        // 持仓 >= Max
//        if (positionCurrent.compareTo(amountHoldMax) != -1) {
//            BigDecimal priceClose = null;
//
//            // 有历史成交
//            if (priceLastMatch != null) {
//                priceClose = priceLastMatch.multiply(percentClose).setScale(scalePrice, RoundingMode.HALF_UP);
//
//                ret = QuantUtil.singleModeOrderSell_ReduceOnly(clientAccountHTTP, symbol, orderIdNewSell, amountOrderClose, priceClose);
//                log.info("mainInit, position > max, placeLimitSell, symbol:{}, orderId:{}, amount:{}, price:{} -> {}, useLastPrice:{}, ret:{}", symbol, orderIdNewSell, amountOrderClose, priceLastMatch, priceClose, true, ret);
//            } else {
//                // 查不到最近一笔订单
//                // priceClose    = priceMarket.multiply(percentClose).setScale(scalePrice, RoundingMode.HALF_UP);
//                // userLastPrice = false;
//
//                // TODO: 告警
//                log.error("mainInit, position > max, placeLimitSell, symbol:{}, orderId:-1, amount:-1, price:-1, useLastPrice:-1, ret:cancelled", symbol);
//            }
//
//            initDone = true;
//            helperBinanceUserEvent.semaphoreLiqPriceUpdate.release();
//            return 0;
//        }
//
//        // 持仓 > Min
//        if (positionCurrent.compareTo(amountHoldMin) != -1) {
//            BigDecimal priceClose    = null;
//            BigDecimal priceOpen     = null;
//            Boolean    userLastPrice = true;
//
//            // 有历史成交
//            if (priceLastMatch != null) {
//                priceClose = priceLastMatch.multiply(percentClose).setScale(scalePrice, RoundingMode.HALF_UP);
//                priceOpen  = priceLastMatch.multiply(percentOpen).setScale(scalePrice, RoundingMode.HALF_DOWN);
//
//                int ret1 = QuantUtil.singleModeOrderSell_ReduceOnly(clientAccountHTTP, symbol, orderIdNewSell, amountOrderClose, priceClose);
//                int ret2 = QuantUtil.singleModeOrderBuy(clientAccountHTTP, symbol, orderIdNewBuy, amountOrderOpen, priceOpen);
//
//                log.info("mainInit, position > min, placeLimitSell, symbol:{}, orderId:{}, amount:{}, price:{} -> {}, useLastPrice:{}, ret:{}", symbol, orderIdNewSell, amountOrderClose, priceLastMatch, priceClose, true, ret1);
//                log.info("mainInit, position > min, placeLimitBuy , symbol:{}, orderId:{}, amount:{}, price:{} -> {}, useLastPrice:{}, ret:{}", symbol, orderIdNewBuy, amountOrderOpen, priceLastMatch, priceOpen, true, ret2);
//            } else {
//                // 查不到最近一笔订单
//                // priceClose    = priceMarket.multiply(percentClose).setScale(scalePrice, RoundingMode.HALF_UP);
//                // priceOpen     = priceMarket.multiply(percentOpen).setScale(scalePrice, RoundingMode.HALF_DOWN);
//                // userLastPrice = false;
//
//                log.error("mainInit, position > min, placeLimitSell, symbol:{}, orderId:-1, amount:-1, price:-1, useLastPrice:-1, ret:cancelled", symbol);
//                log.error("mainInit, position > min, placeLimitBuy , symbol:{}, orderId:-1, amount:-1, price:-1, useLastPrice:-1, ret:cancelled", symbol);
//            }
//
//            initDone = true;
//            helperBinanceUserEvent.semaphoreLiqPriceUpdate.release();
//            return 0;
//        }
//
//        // 持仓<= Min, 市价买入
//        {
//            BigDecimal price  = null;
//            BigDecimal amount = null;
//
//            amount = amountInit.subtract(positionCurrent);
//            amount = amount.max(amountOrderOpen);   // 最少购买一份
//            int ret2 = QuantUtil.singleModeOrderBuy(clientAccountHTTP, symbol, orderIdNewBuy, amount, null);
//            log.info("mainInit, position <= min, placeMarketBuy, symbol:{}, orderId:{}, amount:{}, ret:{}", symbol, orderIdNewBuy, amountOrderOpen, ret2);
//        }
//
//        initDone = true;
//        helperBinanceUserEvent.semaphoreLiqPriceUpdate.release();
//        return 0;
//    }
//
//    public int stop() {
//        initDone = false;
//        doExit   = true;
//        return 0;
//    }
//
//    @Scheduled(fixedRate = 10 * 60 * 1000)
//    private void TaskExtendListenKey() {
//        if (doExit || !initDone || clientAccountHTTP == null || clientWebSocket == null) {
//            return;
//        }
//
//        if (ListenKey == null) {
//            log.info("TaskExtendListenKey wait");
//            return;
//        }
//
//        if (ListenKeyFirstTime) {
//            ListenKeyFirstTime = false;
//            log.info("TaskExtendListenKey ListenKeyFirstTime, will do it next time");
//            return;
//        }
//
//        try {
//            String resp = clientAccountHTTP.userData().extendListenKey();
//            if ("{}".equalsIgnoreCase(resp)) {
//                log.info("TaskExtendListenKey extended success, date:{}", new Date());
//                return;
//            }
//            log.error("TaskExtendListenKey failed");
//        } catch (Exception e) {
//            log.error("TaskExtendListenKey failed, exception:{}, stack:{}", e.getMessage(), e.getStackTrace());
//        }
//    }
//
//    @Scheduled(fixedDelay = 1000)
//    private void TaskStopOrderUpdate() {
//        if (doExit || !initDone || clientAccountHTTP == null || clientWebSocket == null) {
//            return;
//        }
//
//        int numCmd1 = helperBinanceUserEvent.semaphoreLiqPriceUpdate.availablePermits();
//        if (numCmd1 == 0) {
//            return;
//        }
//
//        QuantUtil.waitMs(1000);
//
//        int numCmd2 = helperBinanceUserEvent.semaphoreLiqPriceUpdate.drainPermits();
//        log.info("TaskStopOrderUpdate cmd:{}/{}", numCmd1, numCmd2);
//
//
//        int ret = 0;
//        try {
//            ret = helperBinanceStopOrder.handle(clientAccountHTTP);
//        } catch (Exception e) {
//            log.error("TaskStopOrderUpdate exception:{}, stack:{}", e.getMessage(), e.getStackTrace());
//        }
//    }
//
//    private int threadListenMonitor() {
//        String     resp = null;
//        JSONObject jo   = null;
//        log.info("threadListen started");
//
//        while (!doExit) {
//
//            // create listen key
//            if (ListenKey == null) {
//                try {
//                    resp      = clientAccountHTTP.userData().createListenKey();
//                    jo        = new JSONObject(resp);
//                    ListenKey = jo.getStr("listenKey");
//
//                    log.info("threadListen createListenKey key:{}, resp:{}", ListenKey, resp);
//                } catch (Exception e) {
//                    log.error("threadListen createListenKey failed, error:{}, stack:{}", e.getMessage(), e.getStackTrace());
//                    QuantUtil.waitMs(1000);
//                    continue;
//                }
//            }
//
//            switch (listenStatus) {
//                case Constants.LISTEN_STATUS_STOPPED:
//                case Constants.LISTEN_STATUS_ERROR:
//                    // need reset: yes
//                    break;
//                default:
//                    // need reset: no
//                    QuantUtil.waitMs(500);
//                    continue;
//            }
//
//            // close previous stream if exist
//            if (listenKeyStreamId != -1) {
//                try {
//                    clientWebSocket.closeConnection(listenKeyStreamId);
//                    log.info("threadListen close streamId:{}", listenKeyStreamId);
//                } catch (Exception e) {
//                    log.error("threadListen close streamId failed, streamId:{}, error:{}, stack:{}", listenKeyStreamId, e.getMessage(), e.getStackTrace());
//                    continue;
//                } finally {
//                    log.info("threadListen close streamId done, streamId:{}", listenKeyStreamId);
//                    listenKeyStreamId = -1;
//                }
//            }
//
//            try {
//                listenStatus = Constants.LISTEN_STATUS_STARTING;
//
//                listenKeyStreamId = clientWebSocket.listenUserStream(
//                        ListenKey,
//                        (onOpenMsg) -> {
//                            log.info("threadListen onOpen, status{}->{}, key:{}/{}", listenStatus, Constants.LISTEN_STATUS_RUNNING, listenKeyStreamId, ListenKey);
//                            listenStatus = Constants.LISTEN_STATUS_RUNNING;
//
//                            semaphoreFlowLock.release();
//                        }, (onMsg) -> {
//                            helperBinanceUserEvent.handle(clientAccountHTTP, hashMapBalance2, hashMapPosition, hashTradePriceLast, onMsg);
//                        }, (onClose) -> {
//                            log.info("threadListen onClose, status{}->{}, key:{}/{}", listenStatus, Constants.LISTEN_STATUS_STOPPED, listenKeyStreamId, ListenKey);
//
//                            listenStatus = Constants.LISTEN_STATUS_STOPPED;
//                        }, (error) -> {
//                            log.error("threadListen onError, status{}->{}, key:{}/{}", listenStatus, Constants.LISTEN_STATUS_ERROR, listenKeyStreamId, ListenKey);
//
//                            listenStatus = Constants.LISTEN_STATUS_ERROR;
//                        });
//            } catch (Exception e) {
//                log.error("threadListen onException, status{}->{}, key:{}/{}, error:{}, stack:{}",
//                          listenStatus,
//                          Constants.LISTEN_STATUS_ERROR,
//                          listenKeyStreamId,
//                          ListenKey,
//                          e.getMessage(),
//                          e.getStackTrace());
//
//                listenStatus = Constants.LISTEN_STATUS_ERROR;
//                continue;
//            }
//
//            // 启动中，等待状态变更
//            // starting -> running
//            // starting -> error
//            // starting -> stopped
//            while (listenStatus.equalsIgnoreCase(Constants.LISTEN_STATUS_STARTING)) {
//                QuantUtil.waitMs(500);
//                log.info("threadListen pooling status, key:{}/{}", listenKeyStreamId, ListenKey);
//            }
//
//            switch (listenStatus) {
//                case Constants.LISTEN_STATUS_RUNNING:
//                    log.info("threadListen start success in running, key:{}/{}", listenKeyStreamId, ListenKey);
//                    break;
//                case Constants.LISTEN_STATUS_STOPPED:
//                    log.info("threadListen start failed in stopped, key:{}/{}", listenKeyStreamId, ListenKey);
//                    QuantUtil.waitMs(500);
//                    break;
//                case Constants.LISTEN_STATUS_ERROR:
//                    log.info("threadListen start failed in error, key:{}/{}", listenKeyStreamId, ListenKey);
//                    QuantUtil.waitMs(500);
//                    break;
//            }
//        }
//
//        log.info("threadListen exit");
//        return 0;
//    }
//
//    //@Scheduled(fixedDelay = 5 * 60 * 1000)
//    public void Task24hAveragePrice() {
//        AveragePriceGetResponse averagePrice = helperDaemon.getAveragePrice(symbol);
//        if (averagePrice == null) {
//            log.error("Task24hAveragePrice 24hPrice is null");
//            return;
//        }
//
//        log.info("Task24hAveragePrice 24hPrice:{}", new JSONObject(averagePrice).toStringPretty());
//
//        // TODO: get latest order from DB
//        TOrder lastFilledOrder = orderService.getLastFilledOrder(clientAccountHTTP.getApiKey(), Constants.ExchangeBinance, symbol);
//
//        Long tsLast   = lastFilledOrder.getOrderTimeUpdate();
//        if (tsLast == null) {
//            log.info("Task24hAveragePrice failed, tsLast is null, order:{}", HelperOrder.getBriefInfo(lastFilledOrder).toStringPretty());
//            return;
//        }
//
//        long tsNow    = System.currentTimeMillis();
//        long tsGap    = (tsNow - tsLast) / 1000;
//        long tsGapMax = 24 * 60 * 60;
//
//        tsGapMax = 4 * 60 * 60;
//
//        if (tsGap < tsGapMax) {
//            return;
//        }
//
//        // TODO:
//        log.info("place market buy");
//        log.info("place market buy");
//        log.info("place market buy");
//        log.info("place market buy");
//
//    }
//}
//
