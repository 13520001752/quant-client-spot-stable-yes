package com.magic.service;


import cn.hutool.json.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Data
@Slf4j
public class BinanceOrderHttp {
    private String     orderId;           // "orderId": 1917641,                 // 系统订单号
    private String     orderIdLink;       // "clientOrderId": "abc",             // 用户自定义的订单号
    private BigDecimal cumQuote;          // "cumQuote": "0",                    // 成交金额
    private BigDecimal executeQty;        // "executedQty": "0",                 // 成交量
    private BigDecimal averagePrice;      // "avgPrice": "0.00000",              // 平均成交价
    private BigDecimal originalQty;       // "origQty": "0.40",                  // 原始委托数量
    private String     originalType;      // "origType": "TRAILING_STOP_MARKET", // 触发前订单类型
    private BigDecimal price;             // "price": "0",                   // 委托价格
    private Boolean    reduceOnly;        // "reduceOnly": false,                // 是否仅减仓
    private String     side;              // "side": "BUY",                      // 买卖方向
    private String     positionSide;      // "positionSide": "SHORT", // 持仓方向
    private String     status;            // "status": "NEW",                    // 订单状态
    private BigDecimal stopPrice;         // "stopPrice": "9300",                    // 触发价，对`TRAILING_STOP_MARKET`无效
    private Boolean    closePosition;     // "closePosition": false,   // 是否条件全平仓
    private String     symbol;            // "symbol": "BTCUSDT",                // 交易对
    private long       orderTime;         // "time": 1579276756075,              // 订单时间
    private String     timeInForce;       // "timeInForce": "GTC",               // 有效方法
    private String     orderType;         // "type": "TRAILING_STOP_MARKET",     // 订单类型
    private BigDecimal activatePrice;     // "activatePrice": "9020", // 跟踪止损激活价格, 仅`TRAILING_STOP_MARKET` 订单返回此字段
    private BigDecimal priceRate;         // "priceRate": "0.3", // 跟踪止损回调比例, 仅`TRAILING_STOP_MARKET` 订单返回此字段
    private long       updateTime;        // "updateTime": 1579276756075,        // 更新时间
    private String     workingType;       // "workingType": "CONTRACT_PRICE", // 条件价格触发类型
    private Boolean    priceProtect;      // "priceProtect": false            // 是否开启条件单触发保护


//    订单种类 (orderTypes, type):
//    LIMIT 限价单
//    MARKET 市价单
//    STOP 止损限价单
//    STOP_MARKET 止损市价单
//    TAKE_PROFIT 止盈限价单
//    TAKE_PROFIT_MARKET 止盈市价单
//    TRAILING_STOP_MARKET 跟踪止损单

    public BinanceOrderHttp(
            String orderId,
            String orderIdLink,
            BigDecimal cumQuote,
            BigDecimal executeQty,
            BigDecimal averagePrice,
            BigDecimal originalQty,
            String originalType,
            BigDecimal price,
            Boolean reduceOnly,
            String side,
            String positionSide,
            String status,
            BigDecimal stopPrice,
            Boolean closePosition,
            String symbol,
            long orderTime,
            String timeInForce,
            String orderType,
            BigDecimal activatePrice,
            BigDecimal priceRate,
            long updateTime,
            String workingType,
            Boolean priceProtect
    ) {
        this.orderId       = orderId;
        this.orderIdLink   = orderIdLink;
        this.cumQuote      = cumQuote;
        this.executeQty    = executeQty;
        this.averagePrice  = averagePrice;
        this.originalQty   = originalQty;
        this.originalType  = originalType;
        this.price         = price;
        this.reduceOnly    = reduceOnly;
        this.side          = side;
        this.positionSide  = positionSide;
        this.status        = status;
        this.stopPrice     = stopPrice;
        this.closePosition = closePosition;
        this.symbol        = symbol;
        this.orderTime     = orderTime;
        this.timeInForce   = timeInForce;
        this.orderType     = orderType;
        this.activatePrice = activatePrice;
        this.priceRate     = priceRate;
        this.updateTime    = updateTime;
        this.workingType   = workingType;
        this.priceProtect  = priceProtect;
    }

    public static BinanceOrderHttp getFromJSONObject(JSONObject jo) {
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
        Long       orderTime     = jo.getLong("time");
        String     timeInForce   = jo.getStr("timeInForce");
        String     orderType     = jo.getStr("type");
        BigDecimal activatePrice = jo.getBigDecimal("activatePrice");
        BigDecimal priceRate     = jo.getBigDecimal("priceRate");
        Long       updateTime    = jo.getLong("updateTime");
        String     workingType   = jo.getStr("workingType");
        Boolean    priceProtect  = jo.getBool("priceProtect");

        return new BinanceOrderHttp(
                orderId,
                orderIdLink,
                cumQuote,
                executeQty,
                averagePrice,
                originalQty,
                originalType,
                price,
                reduceOnly,
                side,
                positionSide,
                status,
                stopPrice,
                closePosition,
                symbol,
                orderTime,
                timeInForce,
                orderType,
                activatePrice,
                priceRate,
                updateTime,
                workingType,
                priceProtect
        );
    }
}

//{
//        "e":"ORDER_TRADE_UPDATE",         // 事件类型
//        "E":1568879465651,                // 事件时间
//        "T":1568879465650,                // 撮合时间
//        "o":{
//             "s":"BTCUSDT",                  // 交易对
//             "c":"TEST",                     // 客户端自定订单ID
//             // 特殊的自定义订单ID:
//             // "autoclose-"开头的字符串: 系统强平订单
//             // "adl_autoclose": ADL自动减仓订单
//             // "settlement_autoclose-": 下架或交割的结算订单
//             "S":"SELL",                     // 订单方向
//             "o":"TRAILING_STOP_MARKET", // 订单类型
//             "f":"GTC",                      // 有效方式
//             "q":"0.001",                    // 订单原始数量
//             "p":"0",                        // 订单原始价格
//             "ap":"0",                       // 订单平均价格
//             "sp":"7103.04",                 // 条件订单触发价格，对追踪止损单无效
//             "x":"NEW",                      // 本次事件的具体执行类型
//             "X":"NEW",                      // 订单的当前状态
//             "i":8886774,                    // 订单ID
//             "l":"0",                        // 订单末次成交量
//             "z":"0",                        // 订单累计已成交量
//             "L":"0",                        // 订单末次成交价格
//             "N": "USDT",                    // 手续费资产类型
//             "n": "0",                       // 手续费数量
//             "T":1568879465650,              // 成交时间
//             "t":0,                          // 成交ID
//             "b":"0",                        // 买单净值
//             "a":"9.91",                     // 卖单净值
//             "m": false,                     // 该成交是作为挂单成交吗？
//             "R":false   ,                   // 是否是只减仓单
//             "wt": "CONTRACT_PRICE",         // 触发价类型
//             "ot": "TRAILING_STOP_MARKET",   // 原始订单类型
//             "ps":"LONG"                     // 持仓方向
//             "cp":false,                     // 是否为触发平仓单; 仅在条件订单情况下会推送此字段
//             "AP":"7476.89",                 // 追踪止损激活价格, 仅在追踪止损单时会推送此字段
//             "cr":"5.0",                     // 追踪止损回调比例, 仅在追踪止损单时会推送此字段
//             "pP": false,              // 忽略
//             "si": 0,                  // 忽略
//             "ss": 0,                  // 忽略
//             "rp":"0"                       // 该交易实现盈亏
//        }
//}