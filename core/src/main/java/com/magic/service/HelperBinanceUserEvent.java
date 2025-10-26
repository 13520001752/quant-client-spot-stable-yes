//package com.magic.service;
//
//import cn.hutool.json.JSONArray;
//import cn.hutool.json.JSONObject;
//import com.binance.connector.futures.client.impl.UMFuturesClientImpl;
//import com.magic.constant.Constants;
//import com.magic.emum.BizErrorEnum;
//import com.magic.mybatisplus.entity.TOrder;
//import com.magic.mybatisplus.service.impl.TOrderServiceImpl;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.tuple.Pair;
//import org.json.HTTP;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.actuate.endpoint.jmx.JmxOperationResponseMapper;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.math.RoundingMode;
//import java.util.HashMap;
//import java.util.List;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.Semaphore;
//
//@Slf4j
//@Service
//public class HelperBinanceUserEvent {
////    @Autowired
////    private HelperBinanceLeverageActual helperBinanceLeverageActual;
//    @Autowired
//    private HelperBinanceAccountInfo    helperBinanceAccountInfo;
//    @Autowired
//    private TOrderServiceImpl           orderService;
//
//    @Value("${quant-net1.symbol}")
//    public String symbol;
//
//    @Value("${quant-net1.leverageMaxConfig}")
//    public int leverageMaxConfig;
//    @Value("${quant-net1.leverageMaxAllowed}")
//    public int leverageMaxConfigAllowed;
//
//    // 初始买入数量
//    @Value("${quant-net1.amountInit}")
//    public BigDecimal amountInit;
//
//    @Value("${quant-net1.amountHoldMax}")
//    public BigDecimal amountHoldMax;
//    @Value("${quant-net1.amountHoldMin}")
//    public BigDecimal amountHoldMin;
//
//    @Value("${quant-net1.amountOrderOpen}")
//    public BigDecimal amountOrderOpen;
//    @Value("${quant-net1.amountOrderClose}")
//    public BigDecimal amountOrderClose;
//
//    @Value("${quant-net1.percentOpen}")
//    public BigDecimal percentOpen;
//    @Value("${quant-net1.percentClose}")
//    public BigDecimal percentClose;
//
//    public volatile int       priceScale              = -1;
//    public volatile Semaphore semaphoreLiqPriceUpdate = new Semaphore(0);
//    public volatile Semaphore flowLock                = new Semaphore(0);
//
//    public int handle(
//            UMFuturesClientImpl clientAccountHTTP,
//            ConcurrentHashMap<String, BinanceBalance2> hashMapBalance2,
//            ConcurrentHashMap<String, BinancePosition> hashMapPosition,
//            ConcurrentHashMap<String, BigDecimal> hashTradePriceLast,
//            String msg) {
//        JSONObject jo = new JSONObject(msg);
//
//        //log.info("HelperBinanceUserEvent UserEvent:{}", jo.toStringPretty());
//        String     event        = jo.getStr("e");
//        Long       tsEvent      = jo.getLong("E");
//        Long       tsMatch      = jo.getLong("T");
//        JSONObject eventAccount = jo.getJSONObject("a");    // 账户更新事件
//
//        switch (event) {
//            // 余额及持仓变化
//            case "ACCOUNT_UPDATE":
//                handleUserEventAccountUpdate(clientAccountHTTP, hashMapBalance2, hashMapPosition, eventAccount, tsMatch);
//                break;
//            // 订单变化
//            case "ORDER_TRADE_UPDATE":
//                handleUserEventOrderUpdate(clientAccountHTTP, jo);
//                break;
//            // 账户配置变化，如杠杆倍数
//            case "ACCOUNT_CONFIG_UPDATE":
//                handleUserEventAccountConfigUpdate(jo);
//                break;
//            default:
//                log.info("unhandled message:{}", jo.toStringPretty());
//                break;
//        }
//
//        return 0;
//    }
//
//    private int handleUserEventAccountConfigUpdate(JSONObject jo) {
//        log.info("AccountConfigEvent:{}", jo.toStringPretty());
//        return 0;
//    }
//
//    private int handleUserEventAccountUpdate(
//            UMFuturesClientImpl clientAccountHTTP,
//            ConcurrentHashMap<String, BinanceBalance2> hashMapBalance2,
//            ConcurrentHashMap<String, BinancePosition> hashMapPosition,
//            JSONObject joEvent,
//            Long ts) {
//        JSONArray  ja = null;
//        JSONObject jo = null;
//
//        semaphoreLiqPriceUpdate.release();
//        log.info("AccountUpdate ask to update stop order, ts:{}, event:{}", System.currentTimeMillis(), joEvent.toStringPretty());
//
//        // 订单触发的 余额变动信息
//        ja = joEvent.getJSONArray("B");     // 余额信息
//        if (ja != null && !ja.isEmpty()) {
//            BinanceBalance2 b = null;
//
//            for (int i = 0; i < ja.size(); ++i) {
//                jo = ja.getJSONObject(i);
//                b  = BinanceBalance2.getFromJSONObject(jo, ts);
//
//                //hashMapBalance2.put(b.getCoin(), b);
//                log.info("BalanceUpdate, coin:{}, balance:{}/{}", b.getCoin(), b.getWalletBalance(), b.getWalletBalanceCross());
//            }
//        }
//
//        // 订单触发的 持仓变动
//        ja = joEvent.getJSONArray("P");     // 持仓信息
//        if (ja != null && !ja.isEmpty()) {
//            BinancePosition p = null;
//
//            for (int i = 0; i < ja.size(); ++i) {
//                jo = ja.getJSONObject(i);
//                p  = BinancePosition.getFromWebSocketJSONObject(jo, ts);
//
//                hashMapPosition.put(p.getSymbol(), p);
//                log.info("BalanceUpdate, coin:{}, position:{}", p.getSymbol(), p.getAmountPosition());
//            }
//        }
//        return 0;
//    }
//
//    // 现在时单向持仓，所以只有buy/sell
//    private int handleUserEventOrderUpdate(
//            UMFuturesClientImpl clientAccountHTTP,
//            JSONObject joEvent
//    ) {
//        JSONObject jo = joEvent.getJSONObject("o");
//
//        int    ret         = 0;
//        String symbol      = jo.getStr("s");
//        String orderId     = jo.getStr("i");
//        String orderIdLink = jo.getStr("c");
//        //        String     orderIdLinkReverse = null;
//        String     side          = jo.getStr("S");
//        BigDecimal priceAvg      = jo.getBigDecimal("ap"); // 实际成交价
//        BigDecimal priceOriginal = jo.getBigDecimal("p");  // 委托价格
//        String     orderStatus   = jo.getStr("X");
//        BigDecimal amountFilled  = jo.getBigDecimal("z");  // 订单累计已成交量
//        BigDecimal amountTotal   = jo.getBigDecimal("q");  // 订单原始数量
//        String     orderType     = jo.getStr("o");         // 订单类型
//        String     workingType   = jo.getStr("wt");         // 订单类型
//
//        TOrder orderUpdate = null;
//        // TODO: 获取priceStop
//
//        {
//            // save order info into redis
//            orderUpdate = HelperOrder.parseOrderDataFromWebSocket(clientAccountHTTP.getApiKey(), Constants.ExchangeBinance, jo);
//            orderService.doUpdate(orderUpdate);
//        }
//
//        // 生成订单号，替代手动订单的订单号
//        String orderIdConnect = null;
//
//        String[] split = orderIdLink.split("-");
//        if (split.length != 4) {
//            // 手动单处理逻辑
//
//            // 生成新的订单ID，用来关联手动订单
//            HashMap<String, String> hashMapOrderId = HelperOrder.generateOrderIdLink_Normal(0);
//            switch (side.toUpperCase()) {
//                case "SELL":
//                    orderIdConnect = hashMapOrderId.get("orderIdSell");
//                    break;
//                case "BUY":
//                    orderIdConnect = hashMapOrderId.get("orderIdBuy");
//                    break;
//                default:
//                    log.error("OrderUpdate, unsupported order side:{}", jo.toStringPretty());
//                    break;
//            }
//
//            split = orderIdConnect.split("-");
//            if (split.length != 4) {
//                log.info("OrderUpdate connect failed, orderId:{}, orderIdLink:{}, orderIdConnect:{}, order:{}", orderId, orderIdLink, orderIdConnect, HelperOrder.getBriefInfo(orderUpdate).toStringPretty());
//                return -1;
//            }
//
//            log.info("OrderUpdate connect orderId,        orderId:{}", orderId);
//            log.info("OrderUpdate connect orderId,    orderIdLink:{}", orderIdLink);
//            log.info("OrderUpdate connect orderId, orderIdConnect:{}", orderIdConnect);
//            log.info("OrderUpdate connect orderId,    orderIdLink:{} -> orderIdConnect:{}", orderIdLink, orderIdConnect);
//            // 填入临时生成的orderIdLink
//            orderIdLink = orderIdConnect;
//        }
//
//        switch (split[0]) {
//            case Constants.orderPrefix:
//            case Constants.orderPrefixInitBuy:
//                break;
//            default:
//                log.info("OrderUpdate ignore unrelated order:{}", orderIdLink);
//                break;
//        }
//
//        //                        0   1   2            3
//        // 订单号格式: orderPrefix-seq-b/s-milliseconds
//        String orderPrefix = split[0];  // 前缀，隔离不相关订单
//        String seqString   = split[1];  // 下单批次号
//        String bs          = split[2];  // busy/sell
//        String ts          = split[3];  // 下单的timestamp
//
//        int seqId = Integer.parseInt(seqString);
//
//        //        if (bs.equalsIgnoreCase("b")) {
//        //            orderIdLinkReverse = orderPrefix + "-" + seqString + "-s-" + ts;
//        //        } else {
//        //            orderIdLinkReverse = orderPrefix + "-" + seqString + "-b-" + ts;
//        //        }
//        //
//        //        log.info("OrderUpdate        orderIdLink:{}", orderIdLink);
//        //        log.info("OrderUpdate orderIdLinkReverse:{}", orderIdLinkReverse);
//
//        switch (orderStatus) {
//            case "FILLED":
//                log.info("OrderUpdate process workingType:{} order:{}", workingType, HelperOrder.getBriefInfo(orderUpdate).toStringPretty());
//                break;
//            case "NEW":
//            case "PARTIALLY_FILLED":
//            case "CANCELED":
//            case "EXPIRED":
//            default:
//                log.info("OrderUpdate ignore workingType:{}, order:{}", workingType, HelperOrder.getBriefInfo(orderUpdate).toStringPretty());
//                return 0;
//        }
//
//        boolean isOpen = false;
//        if (bs.equalsIgnoreCase("b")) {
//            isOpen = true;
//        }
//
//        // init buy
//        //        if (split[0].compareToIgnoreCase(Constants.orderPrefixInitBuy) == 0) {
//        //            handleInitBuyFilled(
//        //                    clientAccountHTTP,
//        //                    isOpen,
//        //                    seqId,
//        //                    orderIdLink,
//        //                    orderIdLinkReverse,
//        //                    symbol,
//        //                    percentOpen,
//        //                    percentClose,
//        //                    priceAvg,
//        //                    amountFilled
//        //            );
//        //            return 0;
//        //        }
//
//        // normal buy
//        // 以下代码，只用作处理非初始单的流程
//
//        // 撤销所有订单, 不管当前是否有订单
//        // 这个方法不行，之前设置的止损，止盈订单也同样被撤销
//        ret = QuantUtil.orderCancelAll(clientAccountHTTP, symbol);
//        if (ret != 0) {
//            log.error("OrderUpdate orderCancelAll failed, symbol:{}", symbol);
//            return -1;
//        } else {
//            log.info("OrderUpdate orderCancelAll done, symbol:{}", symbol);
//            QuantUtil.waitMs(50);
//        }
//
//        // 第一次确认，查询现有订单
//        //        int          code      = 0;
//        //        TOrder       order     = null;
//        //        List<TOrder> orderList = null;
//        //
//        //        Pair<Integer, List<TOrder>> retGetOpenAll = QuantUtil.getOpenOrderBySymbol(clientAccountHTTP, symbol);
//        //        code      = retGetOpenAll.getLeft();
//        //        orderList = retGetOpenAll.getRight();
//        //        if (code != 0) {
//        //            log.error("OrderUpdate getOpenOrderBySymbol-1 failed, program stop");
//        //            return -1;
//        //        } else {
//        //            orderList.forEach(o -> {
//        //                log.info("OrderUpdate orderOpen:{}", HelperOrder.getBriefInfo(o));
//        //            });
//        //
//        //            orderService.doUpdate(orderList);
//        //            log.info("OrderUpdate getOpenOrderBySymbol-1 done, symbol:{}, orderNum:{}", symbol, orderList == null ? 0 : orderList.size());
//        //        }
//
//        // 只撤销现价单: orderType = "LIMIT"
//        // 其他的不撤销，比如止损，止盈
//        //        if (orderList != null && !orderList.isEmpty()) {
//        //
//        //            for (int i = 0; i < orderList.size(); ++i) {
//        //                order = orderList.get(i);
//        //
//        //                if (!order.getOrderType().equalsIgnoreCase(Constants.ORDER_TYPE_LIMIT)) {
//        //                    log.info("OrderUpdate 1:1 cancel ignore, orderId:{}/{}, orderType:{}/{}", order.getOrderIdLink(), order.getOrderId(), order.getOrderType(), order.getOrderTypeOriginal());
//        //                } else {
//        //                     ret = QuantUtil.orderCancelByOrderId(clientAccountHTTP, symbol, order.getOrderId());
//        //                    Pair<BizErrorEnum, TOrder> retCancel = QuantUtil.orderCancelByOrderId2(clientAccountHTTP, symbol, orderId);
//        //                    BizErrorEnum               retCode   = retCancel.getKey();
//        //                    TOrder                     orderTmp  = retCancel.getValue();
//        //
//        //                    int retUpdate = orderService.doUpdate(orderTmp);
//        //                    log.info("OrderUpdate 1:1 cancel retCancel:{}, retUpdate:{}, order:{}", retCode, retUpdate, HelperOrder.getBriefInfo(orderTmp));
//        //                }
//        //
//        //                 异常情况
//        //                if (orderList.size() > 3) {
//        //                    QuantUtil.waitMs(50);
//        //                }
//        //            }
//
//        // 第二次确认，查询现有订单
//        //            retGetOpenAll = QuantUtil.getOpenOrderBySymbol(clientAccountHTTP, symbol);
//        //            code          = retGetOpenAll.getLeft();
//        //            orderList     = retGetOpenAll.getRight();
//        //
//        //            if (code == 0 && orderList != null && !orderList.isEmpty()) {
//        //                int numLimit = 0;
//        //
//        //                 统计Limit订单数量
//        //                for (int i = 0; i < orderList.size(); ++i) {
//        //                    order = orderList.get(i);
//        //
//        //                    if (order.getOrderType().equalsIgnoreCase(Constants.ORDER_TYPE_LIMIT)) {
//        //                        ++numLimit;
//        //                    }
//        //                }
//        //
//        //                if (numLimit > 0) {
//        //                     TODO: 报警
//        //                    QuantUtil.orderCancelAll(clientAccountHTTP, symbol);
//        //                    log.error("OrderUpdate limit order not cancelled, do cancelAll, symbol:{}, num:{}/{}, orderList:{}", symbol, orderList.size(), numLimit, orderList);
//        //                }
//        //            }
//        //        }
//
//        // 撤销完成，继续下单
//        // 初始化 余额 & 持仓
//        Pair<BigDecimal, BigDecimal> result          = null;
//        BigDecimal                   balanceCurrent  = null;
//        BigDecimal                   positionCurrent = null;
//
//        result          = helperBinanceAccountInfo.getPositionAndBalanceOnline(clientAccountHTTP, symbol, "USDT");
//        balanceCurrent  = result.getLeft();
//        positionCurrent = result.getRight();
//
//        //        if (balanceCurrent.compareTo(BigDecimal.ZERO) != 1) {
//        //            // TODO: 报警
//        //            log.error("OrderUpdate 余额不足, balance:{}, position:{}", balanceCurrent, positionCurrent);
//        //            log.error("OrderUpdate 余额不足, balance:{}, position:{}", balanceCurrent, positionCurrent);
//        //            log.error("OrderUpdate 余额不足, balance:{}, position:{}", balanceCurrent, positionCurrent);
//        //            log.error("OrderUpdate 余额不足, balance:{}, position:{}", balanceCurrent, positionCurrent);
//        //            return -1;
//        //        }
//
//        boolean canOpen  = false;
//        boolean canClose = false;
//        int     ret1     = 0;
//        int     ret2     = 0;
//
//        BigDecimal priceClose = null;
//        BigDecimal priceOpen  = null;
//
//        // 当前持仓 < 最大持仓, 小于一个订单量
//        if (positionCurrent.add(amountOrderOpen).compareTo(amountHoldMax) != 1) {
//            canOpen = true;
//        }
//
//        // 当前持仓 > 最小持仓, 大于至少一个订单量
//        if (positionCurrent.subtract(amountOrderClose).compareTo(amountHoldMin) != -1) {
//            canClose = true;
//        }
//
//        log.info("2000 OrderUpdate  canOpen:{}, balance:{}, position: Current:{}, Max:{}", canOpen, balanceCurrent, positionCurrent, amountHoldMax);
//        log.info("2000 OrderUpdate canClose:{}, balance:{}, position: Current:{}, Min:{}", canClose, balanceCurrent, positionCurrent, amountHoldMin);
//
//        // 以最后一次成交的价格为基础，上浮平仓，下降开仓
//        // NOTICE: 不能 > 最大值持仓量
//        // NOTICE: 不能 < 最小持仓量
//        // TODO: 需要使用系统提供的价格精度
//        int newScale = priceScale;
//        log.info("OrderUpdate price scale {}:{}", symbol, newScale);
//
//        priceClose = priceAvg.multiply(percentClose).setScale(newScale, RoundingMode.HALF_UP);
//        priceOpen  = priceAvg.multiply(percentOpen).setScale(newScale, RoundingMode.HALF_DOWN);
//        priceAvg   = priceAvg.setScale(newScale, RoundingMode.HALF_UP);
//
//        BigDecimal percentOpenActual  = priceAvg.subtract(priceOpen).divide(priceAvg, 5, RoundingMode.DOWN);
//        BigDecimal percentCloseActual = priceAvg.subtract(priceClose).divide(priceAvg, 5, RoundingMode.DOWN).negate();
//        percentOpenActual  = BigDecimal.ONE.subtract(percentOpenActual);
//        percentCloseActual = BigDecimal.ONE.add(percentCloseActual);
//
//        side = side + ":" + bs;
//
//        // 生成新的订单ID
//        HashMap<String, String> hashMapOrderId  = HelperOrder.generateOrderIdLink_Normal(seqId + 1);
//        String                  orderIdNewOpen  = hashMapOrderId.get("orderIdBuy");
//        String                  orderIdNewClose = hashMapOrderId.get("orderIdSell");
//
//        log.info("");
//        log.info("2000 OrderUpdate prepare order  BUY id:{}, price:{}, percent:{},  canOpen:{}", orderIdNewOpen, priceOpen, percentOpen.toPlainString(), canOpen);
//        log.info("2000 OrderUpdate prepare order SELL id:{}, price:{}, percent:{}, canClose:{}", orderIdNewClose, priceClose, percentClose.toPlainString(), canClose);
//        log.info("");
//
//        if (canClose) {
//            ret1 = QuantUtil.singleModeOrderSell_ReduceOnly(clientAccountHTTP, symbol, orderIdNewClose, amountOrderClose, priceClose);
//            log.info("2000 new close-0: id:{}->{}, price:{} -> {}, amt:{}, %:{}/{}, ret:{}", orderIdLink, orderIdNewClose, priceAvg, priceClose, amountOrderClose, percentClose, percentCloseActual, ret1);
//        } else {
//            log.info("2000 new close-1: id:{}->{}, price:{} -> {}, amt:{}, %:{}/{}, reachMax", orderIdLink, orderIdNewClose, priceAvg, priceClose, amountOrderClose, percentClose, percentCloseActual);
//        }
//
//        if (canOpen) {
//            ret2 = QuantUtil.singleModeOrderBuy(clientAccountHTTP, symbol, orderIdNewOpen, amountOrderOpen, priceOpen);
//            log.info("2000 new  open-0: id:{}->{}, price:{} -> {}, amt:{}, %:{}/{}, ret:{}", orderIdLink, orderIdNewOpen, priceAvg, priceOpen, amountOrderOpen, percentOpen, percentOpenActual, ret2);
//        } else {
//            log.info("2000 new  open-1: id:{}->{}, price:{} -> {}, amt:{}, %:{}/{}, reachMax", orderIdLink, orderIdNewOpen, priceAvg, priceOpen, amountOrderOpen, percentOpen, percentOpenActual);
//        }
//
//        log.info("");
//        return 0;
//    }
//
//    //    public int handleInitBuyFilled(
//    //            UMFuturesClientImpl clientAccountHTTP,
//    //            boolean isOpen,
//    //            int seqId,
//    //            String orderIdLink,         // 第一单
//    //            String orderIdLinkReverse,  // 反向订单号
//    //            String symbol,
//    //            BigDecimal percentBuy,
//    //            BigDecimal percentSell,
//    //            BigDecimal priceAvg,
//    //            BigDecimal amountFilled
//    //    ) {
//    //        priceAvg = priceAvg.setScale(1, RoundingMode.HALF_DOWN);
//    //
//    //        BigDecimal priceHigh = priceAvg;
//    //        BigDecimal priceLow  = priceAvg;
//    //
//    //        log.info("InitGrid     amountHoldMax:{}", amountHoldMax);
//    //        log.info("InitGrid     amountHoldMin:{}", amountHoldMin);
//    //        log.info("InitGrid        amountInit:{}", amountInit);
//    //
//    //        log.info("InitGrid   amountOrderOpen:{}", amountOrderOpen);
//    //        log.info("InitGrid  amountOrderClose:{}", amountOrderClose);
//    //        log.info("InitGrid       percentOpen:{}", percentOpen);
//    //        log.info("InitGrid      percentClose:{}", percentClose);
//    //        log.info("InitGrid      amountFilled:{}", amountFilled);
//    //
//    //        int ret1 = 0;
//    //        int ret2 = 0;
//    //
//    //        // InitGrid 不用撤销任何订单
//    //        log.info("0000 InitGrid order done: id:{}, price:{}, amount:{}", orderIdLink, priceAvg, amountFilled);
//    //
//    //        HashMap<String, String> hashMapOrderId  = HelperOrder.generateOrderIdLink_Normal(seqId + 1);
//    //        String                  orderIdNewOpen  = hashMapOrderId.get("orderIdBuy");
//    //        String                  orderIdNewClose = hashMapOrderId.get("orderIdSell");
//    //
//    //        if )
//    //        priceHigh = priceAvg.multiply(percentOpen).setScale(1, RoundingMode.HALF_DOWN);
//    //        priceLow  = priceAvg.multiply(percentClose).setScale(1, RoundingMode.HALF_DOWN);
//    //
//    //        ret1 = QuantUtil.orderOpen(clientAccountHTTP, symbol, orderIdNewOpen, amountOrderOpen, priceLow);
//    //        ret2 = QuantUtil.orderClose(clientAccountHTTP, symbol, orderIdNewClose, amountOrderClose, priceHigh);
//    //
//    //        log.info("0000 InitGrid CloseNew: id:{}, pricePrev:{}, priceNew:{}, amount:{}, percent:{}, ret:{}", orderIdNewOpen, priceAvg, priceHigh, amountOrderOpen, percentHigh, ret1);
//    //        log.info("0000 InitGrid  OpenNew: id:{}, pricePrev:{}, priceNew:{}, amount:{}, percent:{}, ret:{}", orderIdNewClose, priceAvg, priceLow, amountOrderClose, percentLow, ret2);
//    //        return 0;
//    //    }
//}
//
