package com.magic.service;


import cn.hutool.json.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Data
@Slf4j
public class BinanceOrder {
    private String orderId;
    private String orderIdLink;
    private String symbol;
    private String side;
    private String status;
    private String positionSide;
    private String coinCommission;
    private Long   updateTime;

    public BinanceOrder(
            String orderId,
            String orderIdLink,
            String symbol,
            String side,
            String status,
            String positionSide,
            String coinCommission,
            Long updateTime
    ) {
        this.orderId        = orderId;
        this.orderIdLink    = orderIdLink;
        this.symbol         = symbol;
        this.side           = side;
        this.status         = status;
        this.positionSide   = positionSide;
        this.coinCommission = coinCommission;
        this.updateTime     = updateTime;
    }

    public static BinanceOrder getFromJSONObject(JSONObject jo, Long ts) {
        String orderId        = jo.getStr("i");
        String orderIdLink    = jo.getStr("c");
        String symbol         = jo.getStr("s");
        String side           = jo.getStr("S");
        String status         = jo.getStr("X");
        String positionSide   = jo.getStr("ps");
        String coinCommission = jo.getStr("N");
        Long   updateTime     = ts;

        return new BinanceOrder(
                orderId,
                orderIdLink,
                symbol,
                side,
                status,
                positionSide,
                coinCommission,
                updateTime
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