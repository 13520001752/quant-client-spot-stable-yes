package com.magic.entity;

import cn.hutool.json.JSONObject;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BinanceMiniTicker {
    private long       eventTime;       // 事件时间(毫秒)
    private String     symbol;          // 交易对
    private BigDecimal priceClose;      // 最新成交价格
    private BigDecimal priceOpen;       // 24小时前开始第一笔成交价格
    private BigDecimal priceHigh;       // 24小时内最高成交价
    private BigDecimal priceLow;        // 24小时内最低成交价
    private BigDecimal volume;          // 成交量
    private BigDecimal quantity;        // 成交额


    //   {
    //    "e": "24hrMiniTicker",  // 事件类型
    //    "E": 123456789,         // 事件时间(毫秒)
    //    "s": "BNBUSDT",          // 交易对
    //    "c": "0.0025",          // 最新成交价格
    //    "o": "0.0010",          // 24小时前开始第一笔成交价格
    //    "h": "0.0025",          // 24小时内最高成交价
    //    "l": "0.0010",          // 24小时内最低成交价
    //    "v": "10000",           // 成交量
    //    "q": "18"               // 成交额
    //  }
    public static BinanceMiniTicker parseFromWebsSocketData(JSONObject jo) {
        if (jo == null) {
            return null;
        }

        long       eventTime  = jo.getLong("E");
        String     symbol     = jo.getStr("s");
        BigDecimal priceClose = jo.getBigDecimal("c");
        BigDecimal priceOpen  = jo.getBigDecimal("o");
        BigDecimal priceHigh  = jo.getBigDecimal("h");
        BigDecimal priceLow   = jo.getBigDecimal("l");
        BigDecimal volume     = jo.getBigDecimal("v");
        BigDecimal quantity   = jo.getBigDecimal("q");


        BinanceMiniTicker t = new BinanceMiniTicker();
        t.setEventTime(eventTime);
        t.setSymbol(symbol);
        t.setPriceClose(priceClose);
        t.setPriceOpen(priceOpen);
        t.setPriceHigh(priceHigh);
        t.setPriceLow(priceLow);
        t.setVolume(volume);
        t.setQuantity(quantity);

        return t;
    }
}
