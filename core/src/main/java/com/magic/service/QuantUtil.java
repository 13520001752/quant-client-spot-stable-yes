package com.magic.service;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.binance.connector.client.SpotClient;
import com.binance.connector.client.exceptions.BinanceClientException;
import com.binance.connector.client.impl.SpotClientImpl;
import com.magic.constant.Constants;
import com.magic.emum.BizErrorEnum;
import com.magic.entity.OrderIdLink;
import com.magic.mybatisplus.entity.TOrder;
import com.magic.mybatisplus.service.TOrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Service
public class QuantUtil {
    @Autowired
    TOrderService orderService;

    public static int placeOrderBuy(SpotClient clientAccount, String orderIdLink, String symbol, BigDecimal amount, BigDecimal price) {
        HashMap<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("symbol", symbol);
        parameters.put("side", "BUY");
        parameters.put("quantity", amount.stripTrailingZeros().toPlainString());
        parameters.put("recvWindow", 5000);
        parameters.put("newClientOrderId", orderIdLink);

        if (price == null) {
            parameters.put("type", "MARKET");
        } else {
            parameters.put("type", "LIMIT");
            parameters.put("timeInForce", "GTC");
            parameters.put("price", price.stripTrailingZeros().toPlainString());
        }

        String result = clientAccount.createTrade().newOrder(parameters);
        log.info("placeOrderBuy orderIdLink:{}, price:{}, amount:{}, ret:{}", orderIdLink, price, amount, result);
        return 0;
    }

    public static int placeOrderSell(SpotClient clientAccount, String orderIdLink, String symbol, BigDecimal amount, BigDecimal price) {
        HashMap<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("symbol", symbol);
        parameters.put("side", "SELL");
        parameters.put("quantity", amount.stripTrailingZeros().toPlainString());
        parameters.put("recvWindow", 5000);
        parameters.put("newClientOrderId", orderIdLink);

        if (price == null) {
            parameters.put("type", "MARKET");
        } else {
            parameters.put("type", "LIMIT");
            parameters.put("timeInForce", "GTC");
            parameters.put("price", price.stripTrailingZeros().toPlainString());
        }

        String result = null;
        try {
            result = clientAccount.createTrade().newOrder(parameters);
        } catch (BinanceClientException e) {
            log.error("placeOrderSell failed1, orderIdLink:{}, price:{}, amount:{}", orderIdLink, price, amount);
            log.error("placeOrderSell failed1", e);
            return e.getErrorCode();
        } catch (Exception e) {
            log.error("placeOrderSell failed2, orderIdLink:{}, price:{}, amount:{}", orderIdLink, price, amount);
            log.error("placeOrderSell failed2", e);
        }

        return 0;
    }

    public static JSONObject orderCancel(SpotClient clientAccount, String symbol, String orderId) {
        JSONObject jo = new JSONObject();

        if (StringUtils.isBlank(orderId)) {
            log.error("orderCancelByOrderId bad orderId, symbol:{}", symbol);
            return jo;
        }

        Map<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("symbol", symbol);
        parameters.put("orderId", orderId);
        parameters.put("recvWindow", 5000);

        String result = clientAccount.createTrade().cancelOrder(parameters);

        jo = new JSONObject(result);
        log.info("orderCancel success :{}", jo);
        return jo;
    }

    public static int orderCancelAll(SpotClient clientAccount, String symbol) {
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("symbol", symbol);

        String result = null;

        JSONObject jo = null;
        try {
            result = clientAccount.createTrade().cancelOpenOrders(parameters);

        } catch (BinanceClientException e) {
            if (-2011 == e.getErrorCode()) {
                log.info("orderCancelAll success, retCode:{} retMsg:{}", e.getErrorCode(), e.getMessage());
                return 0;
            }

            log.error("orderCancelAll failed1, error:", e);
            return -1;
        } catch (Exception e) {
            log.error("orderCancelAll failed2, result:{}, error:", result, e);
            return -1;
        }

        JSONArray ja = new JSONArray(result);
        log.info("orderCancel success, resp:{}", ja.toStringPretty());
        return 0;
    }

    public static JSONArray getOpenOrderBySymbol(SpotClient clientAccount, String symbol) {
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("symbol", symbol);
        parameters.put("recvWindow", 5000);

        String    result = clientAccount.createTrade().getOpenOrders(parameters);
        JSONArray ja     = new JSONArray(result);

        return ja;
    }

    public static Pair<BizErrorEnum, TOrder> getOrderByOrderId(SpotClient clientAccount, String symbol, String orderId) {
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("symbol", symbol);
        parameters.put("orderId", orderId);
        parameters.put("recvWindow", 5000);

        String result = null;
        try {
            result = clientAccount.createTrade().getOrder(parameters);
        } catch (Exception e) {
            log.error("getOpenOrderByOrderId exception:{}, param:{}", e.getMessage(), new JSONObject(parameters));
            return Pair.of(BizErrorEnum.ERROR_ORDER_QUERY, null);
        }

        JSONObject jo = null;
        try {
            jo = new JSONObject(result);
        } catch (Exception e) {
            log.error("getOpenOrderByOrderId failed, exception in parsing json string:{}, exception:{}, stack:{}", result, e.getMessage(), e.getStackTrace());
            return Pair.of(BizErrorEnum.EXCEPTION_JSON_PARSE, null);
        }

        // TOrder order = TOrderServiceImpl.parse(clientAccount.getApiKey(), Constants.ExchangeBinance, jo);
        TOrder order = HelperOrder.parseOrderDataFromHttp(Constants.ExchangeBinance, jo);

        log.info("getOpenOrderByOrderId success, resp:{}", jo.toStringPretty());
        return Pair.of(BizErrorEnum.SUCCESS, order);
    }

    public static Pair<Integer, BigDecimal> getPriceLatest(SpotClient clientAccount, String symbol) {
        if (StringUtils.isBlank(symbol)) {
            log.error("getPriceLatest bad symbol:{}", symbol);
            return Pair.of(-1, null);
        }

        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("symbol", symbol);
        parameters.put("recvWindow", 5000);

        String resp = null;
        try {
            resp = clientAccount.createMarket().tickerSymbol(parameters);
        } catch (Exception e) {
            log.error("getPriceLatest failed, param:{}, exception:{}, stack:{}", parameters, e.getMessage(), e.getStackTrace());
            return Pair.of(-2, null);
        }

        JSONObject jo = null;
        try {
            jo = new JSONObject(resp);
        } catch (Exception e) {
            log.error("getPriceLatest failed, exception in parsing json string:{}, exception:{}, stack:{}", resp, e.getMessage(), e.getStackTrace());
            return Pair.of(-3, null);
        }

        //        {
        //            "symbol": "XXXXUSDT",
        //            "price": "70.570",
        //            "time": 1689001185104
        //        }

        BigDecimal price = jo.getBigDecimal("price");
        if (price == null) {
            log.error("getPriceLatest failed, bad price, resp:{}", jo.toStringPretty());
            return Pair.of(-4, null);
        }

        log.info("getPriceLatest success, resp:{}", jo.toStringPretty());
        return Pair.of(0, price);
    }

    // buy
    // sell
    //    public static HashMap<String, String> generateOrderIdLink(int seqId) {
    //        Long ts = System.currentTimeMillis();
    //
    //        String orderIdBuy  = String.format("%s-%d-%s-%d", Constants.orderPrefix, seqId, "b", ts);
    //        String orderIdSell = String.format("%s-%d-%s-%d", Constants.orderPrefix, seqId, "s", ts);
    //
    //        HashMap<String, String> map = new HashMap<>();
    //        map.put("orderIdBuy", orderIdBuy);
    //        map.put("orderIdSell", orderIdSell);
    //        return map;
    //    }

    public static void waitMs(long ts) {
        try {
            Thread.sleep(ts);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
