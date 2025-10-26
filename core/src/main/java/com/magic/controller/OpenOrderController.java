package com.magic.controller;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.binance.connector.client.SpotClient;
import com.magic.constant.Constants;
import com.magic.emum.BizErrorEnum;
import com.magic.mybatisplus.entity.TConfig;
import com.magic.mybatisplus.entity.TSymbolConfig;
import com.magic.mybatisplus.mapper.TConfigMapper;
import com.magic.mybatisplus.mapper.TOrderMapper;
import com.magic.mybatisplus.mapper.TSymbolConfigMapper;
import com.magic.mybatisplus.service.impl.TOrderServiceImpl;
import com.magic.service.*;
import com.magic.vo.resp.base.ResponseBase;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/open-order")
public class OpenOrderController {
    @Value("${quant.configId}")
    private Integer configIdFromConfigFile;

    @Autowired
    TOrderServiceImpl   orderService;
    @Autowired
    TConfigMapper       configMapper;
    @Autowired
    TOrderMapper        orderMapper;
    @Autowired
    TSymbolConfigMapper symbolConfigMapper;
    @Autowired
    CommonService       commonService;

    // orderId == null: 查询所有
    // orderId != null: 查询指定
    @GetMapping("/get")
    public synchronized ResponseBase getOrderOpen(
            @RequestParam(required = true, name = "configId") Integer configId,
            @RequestParam(required = false, name = "order-id") String orderId
    ) {
        TConfig    config            = commonService.getConfig(configId);
        SpotClient clientAccountHTTP = commonService.getClientAccountHTTP(configId);
        String     symbol            = config.getSymbol();

        // 撤销所有订单
        JSONArray ja = QuantUtil.getOpenOrderBySymbol(clientAccountHTTP, symbol);
        log.info("3000 getOpenOrderByOrderId success, resp:{}", ja.toStringPretty());
        return ResponseBase.success(ja);
    }

    // orderId == null: 取消所有
    // orderId != null: 取消指定
    @GetMapping("/cancel")
    public ResponseBase cancelOrderOpen(
            @RequestParam(required = true, name = "configId") Integer configId,
            @RequestParam(required = false, name = "order-id") String orderId
    ) {
        JSONObject joResp = new JSONObject();

        TConfig    config            = commonService.getConfig(configId);
        SpotClient clientAccountHTTP = commonService.getClientAccountHTTP(configId);
        String     symbol            = config.getSymbol();

        if (clientAccountHTTP == null) {
            log.error("3000 http client isn't init");
            return new ResponseBase(BizErrorEnum.ERROR_NOT_INIT_CLIENT);
        }

        if (StringUtils.isBlank(symbol)) {
            log.error("3000 symbol isn't init");
            return new ResponseBase(BizErrorEnum.ERROR_NOT_INIT_CLIENT);
        }

        // 撤销指定订单
        if (StringUtils.isNotBlank(orderId)) {
            JSONObject jo = QuantUtil.orderCancel(clientAccountHTTP, symbol, orderId);
            log.info("3000 cancelAllOpenOrder orderIds:{}, ret:{}", orderId, jo.toStringPretty());
            return new ResponseBase(BizErrorEnum.SUCCESS, jo);
        }

        // 撤销所有订单
        int ret = QuantUtil.orderCancelAll(clientAccountHTTP, symbol);
        if (ret != 0) {
            log.info("3000 cancel all failed, symbol:{}, ret:{}", symbol, ret);
        } else {
            log.info("3000 cancel all success, symbol:{}, ret:{}, sleep 300ms", symbol, ret);
            QuantUtil.waitMs(300);
        }

        return ResponseBase.GetResponseSuccess();
    }

    @GetMapping("/open")
    public ResponseBase placeOrderBuy(
            @RequestParam(required = true, name = "configId") Integer configId,
            @RequestParam(required = true, name = "amount") BigDecimal amount,
            @RequestParam(required = false, name = "price") BigDecimal price
    ) {
        SpotClient    clientAccountHTTP = commonService.getClientAccountHTTP(configId);
        TConfig       config            = commonService.getConfig(configId);
        TSymbolConfig symbolConfig      = Constants.getSymbolConfig(config.getSymbol());
        String        symbol            = config.getSymbol().toUpperCase();
        int           scalePrice        = symbolConfig.getTickSize();

        if (clientAccountHTTP == null) {
            log.info("3000 http client isn't init");
            return new ResponseBase(BizErrorEnum.ERROR_NOT_INIT_CLIENT);
        }

        if (StringUtils.isBlank(symbol)) {
            log.info("3000 symbol isn't init");
            return new ResponseBase(BizErrorEnum.ERROR_NOT_INIT_SYMBOL);
        }

        if (scalePrice <= 0) {
            log.info("3000 symbol isn't init");
            return new ResponseBase(BizErrorEnum.ERROR_NOT_INIT_PRICE_SCALE);
        }

        if (price != null) {
            BigDecimal priceNew = price.setScale(scalePrice, RoundingMode.UP);
            log.info("3000 openOrder price: {} -> {}", price, priceNew);
            price = priceNew;
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) != 1) {
            log.info("3000 amount isn't valid");
            return new ResponseBase(BizErrorEnum.BAD_PARAM_AMOUNT);
        }

        // 生成订单号
        HashMap<String, String> mapTmp        = HelperOrder.generateOrderIdLink_Normal(configId, 1);
        String                  orderIdNewBuy = mapTmp.get("orderIdBuy");

        // 生成下单参数
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("symbol", symbol);
        parameters.put("quantity", amount);
        parameters.put("side", "BUY");
        parameters.put("newClientOrderId", orderIdNewBuy);
        parameters.put("recvWindow", 5000);

        // 市价单
        if (price == null) {
            parameters.put("type", "MARKET");
        } else {
            // 限价单
            parameters.put("type", "LIMIT");
            parameters.put("timeInForce", "GTC");
            parameters.put("price", price);
        }

        String s = clientAccountHTTP.createTrade().newOrder(parameters);
        JSONObject jo = new JSONObject(s);

        log.info("3000 placeBuy resp:{}", jo.toStringPretty());
        return ResponseBase.success(jo);
    }

    @GetMapping("/close")
    public ResponseBase placeOrderSell(
            @RequestParam(required = true, name = "configId") Integer configId,
            @RequestParam(required = true, name = "amount") BigDecimal amount,
            @RequestParam(required = false, name = "price") BigDecimal price
    ) {
        SpotClient    clientAccountHTTP = commonService.getClientAccountHTTP(configId);
        TConfig       config            = commonService.getConfig(configId);
        TSymbolConfig symbolConfig      = Constants.getSymbolConfig(config.getSymbol());
        String        symbol            = config.getSymbol().toUpperCase();
        int           scalePrice        = symbolConfig.getTickSize();

        if (clientAccountHTTP == null) {
            log.info("3000 closeOrder http client isn't init");
            return new ResponseBase(BizErrorEnum.ERROR_NOT_INIT_CLIENT);
        }

        if (StringUtils.isBlank(symbol)) {
            log.info("3000 closeOrder symbol isn't init");
            return new ResponseBase(BizErrorEnum.ERROR_NOT_INIT_SYMBOL);
        }

        if (scalePrice <= 0) {
            log.info("3000 closeOrder symbol isn't init");
            return new ResponseBase(BizErrorEnum.ERROR_NOT_INIT_PRICE_SCALE);
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) != 1) {
            log.info("3000 amount isn't valid");
            return new ResponseBase(BizErrorEnum.BAD_PARAM_AMOUNT);
        }

        // 生成订单号
        HashMap<String, String> mapTmp         = HelperOrder.generateOrderIdLink_Normal(configId, 1);
        String                  orderIdNewSell = mapTmp.get("orderIdSell");

        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("symbol", symbol);
        parameters.put("side", "SELL");
        parameters.put("quantity", amount);
        parameters.put("newClientOrderId", orderIdNewSell);
        // 避免由于下单，产生反向持仓
        parameters.put("reduceOnly", true);
        parameters.put("recvWindow", 5000);

        // 市价单
        if (price == null) {
            parameters.put("type", "MARKET");
        } else {
            // 限价单
            parameters.put("type", "LIMIT");
            parameters.put("timeInForce", "GTC");
            parameters.put("price", price);
        }

        String s = clientAccountHTTP.createTrade().newOrder(parameters);
        JSONObject jo = new JSONObject(s);

        log.info("3000 closeOrder resp:{}", jo.toStringPretty());
        return ResponseBase.success(jo);
    }

    @GetMapping("/stop-loss")
    public ResponseBase stopLossOrder(
            @RequestParam(required = true, name = "configId") Integer configId,
            @RequestParam(required = true, name = "amount") BigDecimal amount,
            @RequestParam(required = false, name = "price") BigDecimal stopPrice
    ) {
        return ResponseBase.success(new JSONObject());
    }

    @GetMapping("/get-last-match-price")
    public ResponseBase getLastMatchPrice(
            @RequestParam(required = true, name = "configId") Integer configId
    ) {
        return ResponseBase.success();
    }
}
