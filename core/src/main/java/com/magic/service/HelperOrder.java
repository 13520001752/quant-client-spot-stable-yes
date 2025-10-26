package com.magic.service;

import cn.hutool.json.JSONObject;
import com.magic.constant.Constants;
import com.magic.entity.OrderIdLink;
import com.magic.mybatisplus.entity.TOrder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;

@Slf4j
@Service
public class HelperOrder {
    // buy
    // sell
    // 订单号生成：常规订单
    public static HashMap<String, String> generateOrderIdLink_Normal(int configId, int seqId) {
        Long ts = System.currentTimeMillis();

        String orderIdBuy  = String.format("%s-%d-%d-%s", configId, ts, seqId, "b");
        String orderIdSell = String.format("%s-%d-%d-%s", configId, ts, seqId, "s");

        HashMap<String, String> map = new HashMap<>();
        map.put("orderIdBuy", orderIdBuy);
        map.put("orderIdSell", orderIdSell);
        return map;
    }

    // 订单号生成：常规订单
    public static HashMap<String, String> generateOrderIdLink_Normal(int configId, int seqId, long ts) {
        String orderIdBuy  = String.format("%s-%d-%d-%s", configId, ts, seqId, "b");
        String orderIdSell = String.format("%s-%d-%d-%s", configId, ts, seqId, "s");

        HashMap<String, String> map = new HashMap<>();
        map.put("orderIdBuy", orderIdBuy);
        map.put("orderIdSell", orderIdSell);
        return map;
    }

    //    // 订单号生成：初始订单
    //    public static HashMap<String, String> generateOrderIdLink_Init(int seqId) {
    //        Long ts = System.currentTimeMillis();
    //
    //        String orderIdBuy  = String.format("%s-%d-%s-%d", Constants.orderPrefixInitBuy, seqId, "b", ts);
    //        String orderIdSell = String.format("%s-%d-%s-%d", Constants.orderPrefixInitBuy, seqId, "s", ts);
    //
    //        HashMap<String, String> map = new HashMap<>();
    //        map.put("orderIdBuy", orderIdBuy);
    //        map.put("orderIdSell", orderIdSell);
    //        return map;
    //    }

    // 订单号生成：初始订单
    public static String generateOrderIdLink_Stop(int configId, int seqId) {
        Long ts = System.currentTimeMillis();

        String orderIdBuy = String.format("%s-%d-%d-%s", configId, ts, seqId, "stop");

        return orderIdBuy;
    }

    public static TOrder parseOrderDataFromHttp(int exchangeId, JSONObject jo) {
        TOrder order = new TOrder();

        String     orderId       = jo.getStr("orderId");
        String     orderIdLink   = jo.getStr("clientOrderId");
        BigDecimal cumQuote      = jo.getBigDecimal("cumQuote");
        BigDecimal executeQty    = jo.getBigDecimal("executedQty");
        BigDecimal averagePrice  = jo.getBigDecimal("avgPrice");
        BigDecimal originalQty   = jo.getBigDecimal("origQty");
        String     originalType  = jo.getStr("origType");
        BigDecimal price         = jo.getBigDecimal("price");
        Boolean    reduceOnly    = jo.getBool("reduceOnly");
        String     side          = jo.getStr("side");
        String     positionSide  = jo.getStr("positionSide");
        String     status        = jo.getStr("status");
        BigDecimal stopPrice     = jo.getBigDecimal("stopPrice");
        Boolean    closePosition = jo.getBool("closePosition");
        String     symbol        = jo.getStr("symbol");
        Long       orderTime     = jo.getLong("time");          // 下单时间
        Long       updateTime    = jo.getLong("updateTime");    // 更新时间
        String     timeInForce   = jo.getStr("timeInForce");
        String     orderType     = jo.getStr("type");
        //BigDecimal activatePrice = jo.getBigDecimal("activatePrice");
        //BigDecimal priceRate     = jo.getBigDecimal("priceRate");
        //String     workingType   = jo.getStr("workingType");
        //Boolean    priceProtect  = jo.getBool("priceProtect");

        if (StringUtils.isBlank(orderId)) {
            log.error("OrderInfoHttp invalid, jo:{}", jo.toStringPretty());
            return null;
        }

        order.setOrderId(orderId);
        order.setOrderIdLink(orderIdLink);
        order.setExchangeId(exchangeId);
        order.setApiKey("");
        order.setSymbol(symbol);
        order.setOrderSide(side);
        order.setOrderType(orderType);
        order.setOrderTypeOriginal(originalType);
        order.setOrderPriceOriginal(price);
        order.setOrderPriceAverage(averagePrice);
        order.setOrderQuantityOriginal(originalQty);
        order.setOrderQuantityExecute(executeQty);
        order.setCumulativeQuote(cumQuote);
        order.setClosePosition(closePosition);
        order.setPriceStop(stopPrice);
        order.setReduceOnly(reduceOnly);
        order.setTimeInForce(timeInForce);
        order.setPositionSide(positionSide);
        order.setOrderStatus(status);
        order.setOrderTimeCreate(orderTime);
        order.setOrderTimeUpdate(updateTime);
        order.setDeleted(Constants.DELETED_NO);

        OrderIdLink idLink = OrderIdLink.getInstance(orderIdLink);
        if (idLink != null) {
            order.setTrackId(idLink.getSequenceId());
            order.setTrackTimestamp(idLink.getTimestamp());
        } else {
            order.setTrackId(-1);
            order.setTrackTimestamp(-1L);
        }
        return order;
    }

    public static TOrder parseOrderDataFromWebSocket(int exchangeId, JSONObject jo) {
        TOrder order = new TOrder();

        //        String coinCommission = jo.getStr("N");

        String     orderId       = jo.getStr("i");                     // "i":8886774, 订单ID
        String     orderIdLink   = jo.getStr("c");
        BigDecimal cumQuote      = null;                                 // 成交金额: 推送没有
        BigDecimal executeQty    = jo.getBigDecimal("z");            // "z":"0",订单累计已成交量
        BigDecimal averagePrice  = jo.getBigDecimal("ap");           // "ap":"0",订单平均价格
        BigDecimal originalQty   = jo.getBigDecimal("q");            // "q":"0.001", 订单原始数量
        String     originalType  = jo.getStr("ot");                   // "o":"TRAILING_STOP_MARKET", 订单类型
        BigDecimal price         = jo.getBigDecimal("p");            // "p":"0", 订单原始价格
        Boolean    reduceOnly    = jo.getBool("R");                 // "R":false, 是否是只减仓单
        String     side          = jo.getStr("S");                  // "S":"SELL", 订单方向
        String     positionSide  = jo.getStr("ps");                 // "ps":"LONG", 持仓方向
        String     status        = jo.getStr("X");                  // "X":"NEW", 订单的当前状态
        BigDecimal stopPrice     = jo.getBigDecimal("sp");          // "sp":"7103.04", 条件订单触发价格，对追踪止损单无效
        Boolean    closePosition = jo.getBool("cp");                // 是否为触发平仓单; 仅在条件订单情况下会推送此字段
        String     symbol        = jo.getStr("s");
        Long       orderTime     = null;                                 // 推送没有
        Long       updateTime    = jo.getLong("T");                 // "T":1568879465650, 成交时间
        String     timeInForce   = jo.getStr("f");                  // "f":"GTC", 有效方式
        String     orderType     = jo.getStr("o");
        // BigDecimal activatePrice = jo.getBigDecimal("activatePrice"); // "ap":"0", 订单平均价格
        // BigDecimal priceRate     = jo.getBigDecimal("priceRate");
        // String     workingType   = jo.getStr("wt");        // "wt": "CONTRACT_PRICE", 触发价类型
        // Boolean    priceProtect  = jo.getBool("priceProtect");

        if (StringUtils.isBlank(orderId)) {
            log.error("OrderInfoWSS invalid, jo:{}", jo.toStringPretty());
            return null;
        }

        order.setOrderId(orderId);
        order.setOrderIdLink(orderIdLink);
        order.setExchangeId(exchangeId);
        order.setApiKey("");
        order.setSymbol(symbol);
        order.setOrderSide(side);
        order.setOrderType(orderType);
        order.setOrderTypeOriginal(originalType);
        order.setOrderPriceOriginal(price);
        order.setOrderPriceAverage(averagePrice);
        order.setOrderQuantityOriginal(originalQty);
        order.setOrderQuantityExecute(executeQty);
        order.setCumulativeQuote(null);
        order.setClosePosition(closePosition);
        order.setPriceStop(stopPrice);
        order.setReduceOnly(reduceOnly);
        order.setTimeInForce(timeInForce);
        order.setPositionSide(positionSide);
        order.setOrderStatus(status);
        order.setOrderTimeCreate(null);
        order.setOrderTimeUpdate(updateTime);
        order.setDeleted(Constants.DELETED_NO);

        OrderIdLink idLink = OrderIdLink.getInstance(orderIdLink);
        if (idLink != null) {
            order.setTrackId(idLink.getSequenceId());
            order.setTrackTimestamp(idLink.getTimestamp());
        } else {
            order.setTrackId(-1);
            order.setTrackTimestamp(-1L);
        }
        return order;
    }

    public static JSONObject getBriefInfo(TOrder order) {
        JSONObject jo = new JSONObject();

        jo.put("orderId", order.getOrderId());
        jo.put("OrderIdLink", order.getOrderIdLink());
        jo.put("OrderTypeOriginal", order.getOrderTypeOriginal());
        jo.put("OrderType", order.getOrderType());
        jo.put("OrderPriceOriginal", order.getOrderPriceOriginal());
        jo.put("OrderPriceAverage", order.getOrderPriceAverage());
        jo.put("PriceStop", order.getPriceStop());
        jo.put("QuantityOriginal", order.getOrderQuantityOriginal());
        jo.put("QuantityExecute", order.getOrderQuantityExecute());
        jo.put("OrderStatus", order.getOrderStatus());
        return jo;
    }
}

