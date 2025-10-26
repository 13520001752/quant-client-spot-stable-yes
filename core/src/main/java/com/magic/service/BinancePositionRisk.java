package com.magic.service;


import cn.hutool.json.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;


// https://binance-docs.github.io/apidocs/futures/cn/#v2-user_data-3

//   "entryPrice": "0.00000",          // 开仓均价
//   "marginType": "isolated",         // 逐仓模式或全仓模式
//   "isAutoAddMargin": "false",       //
//   "isolatedMargin": "0.00000000",   // 逐仓保证金
//   "leverage": "10",                 // 当前杠杆倍数
//   "liquidationPrice": "0",          // 参考强平价格
//   "markPrice": "6679.50671178",     // 当前标记价格
//   "maxNotionalValue": "20000000",   // 当前杠杆倍数允许的名义价值上限
//   "positionAmt": "0.000",           // 头寸数量，符号代表多空方向, 正数为多，负数为空
//   "notional": "0"              ,    //
//   "isolatedWallet": "0",            //
//   "symbol": "BTCUSDT",              // 交易对
//   "unRealizedProfit": "0.00000000", // 持仓未实现盈亏
//   "positionSide": "BOTH",           // 持仓方向
//   "updateTime": 1625474304765       // 更新时间

@Data
@Slf4j
public class BinancePositionRisk {
    private BigDecimal entryPrice;
    private String     marginType;
    private Boolean    isAutoAddMargin;
    private BigDecimal isolatedMargin;
    private Integer    leverage;
    private BigDecimal liquidationPrice;
    private BigDecimal markPrice;
    private BigDecimal maxNotionalValue;
    private BigDecimal positionAmt;
    private BigDecimal notional;
    private Integer    isolatedWallet;
    private String     symbol;
    private BigDecimal unRealizedProfit;
    private String     positionSide;
    private BigDecimal updateTime;

    public BinancePositionRisk(
            BigDecimal entryPrice,
            String marginType,
            Boolean isAutoAddMargin,
            BigDecimal isolatedMargin,
            Integer leverage,
            BigDecimal liquidationPrice,
            BigDecimal markPrice,
            BigDecimal maxNotionalValue,
            BigDecimal positionAmt,
            BigDecimal notional,
            Integer isolatedWallet,
            String symbol,
            BigDecimal unRealizedProfit,
            String positionSide,
            BigDecimal updateTime
    ) {
        this.entryPrice       = entryPrice;            // 开仓均价
        this.marginType       = marginType;            // 逐仓模式或全仓模式
        this.isAutoAddMargin  = isAutoAddMargin;       //
        this.isolatedMargin   = isolatedMargin;        // 逐仓保证金
        this.leverage         = leverage;              // 当前杠杆倍数
        this.liquidationPrice = liquidationPrice;      // 参考强平价格
        this.markPrice        = markPrice;             // 当前标记价格
        this.maxNotionalValue = maxNotionalValue;      // 当前杠杆倍数允许的名义价值上限
        this.positionAmt      = positionAmt;           // 头寸数量，符号代表多空方向, 正数为多，负数为空
        this.notional         = notional;              //
        this.isolatedWallet   = isolatedWallet;        //
        this.symbol           = symbol;                // 交易对
        this.unRealizedProfit = unRealizedProfit;      // 持仓未实现盈亏
        this.positionSide     = positionSide;          // 持仓方向
        this.updateTime       = updateTime;            // 更新时间
    }

    // websocket订单/余额变动推送
    public static BinancePositionRisk getFromHttpRequestJSONObject(JSONObject jo) {
        BigDecimal entryPrice       = jo.getBigDecimal("entryPrice");        //   "entryPrice": "0.00000",          // 开仓均价                                                                                                                                                                                           //     交易对
        String     marginType       = jo.getStr("marginType");               //   "marginType": "isolated",         // 逐仓模式或全仓模式
        Boolean    isAutoAddMargin  = jo.getBool("isAutoAddMargin");         //   "isAutoAddMargin": "false",       //
        BigDecimal isolatedMargin   = jo.getBigDecimal("isolatedMargin");    //   "isolatedMargin": "0.00000000",   // 逐仓保证金
        Integer    leverage         = jo.getInt("leverage");                 //   "leverage": "10",                 // 当前杠杆倍数
        BigDecimal liquidationPrice = jo.getBigDecimal("liquidationPrice");  //   "liquidationPrice": "0",          // 参考强平价格
        BigDecimal markPrice        = jo.getBigDecimal("markPrice");         //   "markPrice": "6679.50671178",     // 当前标记价格
        BigDecimal maxNotionalValue = jo.getBigDecimal("maxNotionalValue");  //   "maxNotionalValue": "20000000",   // 当前杠杆倍数允许的名义价值上限
        BigDecimal positionAmt      = jo.getBigDecimal("positionAmt");       //   "positionAmt": "0.000",           // 头寸数量，符号代表多空方向, 正数为多，负数为空
        BigDecimal notional         = jo.getBigDecimal("notional");          //   "notional": "0"              ,    //
        Integer    isolatedWallet   = jo.getInt("isolatedWallet");           //   "isolatedWallet": "0",            //
        String     symbol           = jo.getStr("symbol");                   //   "symbol": "XXXUSDT",              // 交易对
        BigDecimal unRealizedProfit = jo.getBigDecimal("unRealizedProfit");  //   "unRealizedProfit": "0.00000000", // 持仓未实现盈亏
        String     positionSide     = jo.getStr("positionSide");             //   "positionSide": "BOTH",           // 持仓方向
        BigDecimal updateTime       = jo.getBigDecimal("updateTime");        //   "updateTime": 1625474304765       // 更新时间

        return new BinancePositionRisk(
                entryPrice,
                marginType,
                isAutoAddMargin,
                isolatedMargin,
                leverage,
                liquidationPrice,
                markPrice,
                maxNotionalValue,
                positionAmt,
                notional,
                isolatedWallet,
                symbol,
                unRealizedProfit,
                positionSide,
                updateTime
        );
    }
}
