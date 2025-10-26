//package com.magic.service;
//
//import cn.hutool.json.JSONObject;
//import com.baomidou.mybatisplus.annotation.TableField;
//import com.magic.constant.Constants;
//import com.magic.entity.OrderIdLink;
//import com.magic.mybatisplus.entity.TKline;
//import com.magic.mybatisplus.entity.TOrder;
//import io.netty.util.Constant;
//import io.swagger.annotations.ApiModelProperty;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.logging.log4j.message.TimestampMessage;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.math.RoundingMode;
//import java.time.LocalDateTime;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.TimeZone;
//
//@Slf4j
//@Service
//public class HelperKline {
//    @Autowired
//    QuantService quantService;
//
//    public static TKline parseFromHttp(int exchangeId, JSONObject jo) {
//        return null;
//    }
//
//    public static TKline parseKLineDataFromWebSocket(String msg) {
//        JSONObject jo = new JSONObject(msg);
//
//        JSONObject joData      = jo.getJSONObject("data");
//        JSONObject joDataKline = joData.getJSONObject("k");
//
//        String     symbol         = joData.getStr("ps");
//        String     klineInterval  = joDataKline.getStr("i");
//        BigDecimal priceHigh      = joDataKline.getBigDecimal("h");
//        BigDecimal priceLow       = joDataKline.getBigDecimal("l");
//        BigDecimal priceOpen      = joDataKline.getBigDecimal("o");
//        BigDecimal priceClose     = joDataKline.getBigDecimal("c");
//        BigDecimal volume         = joDataKline.getBigDecimal("v");
//        Long       timestamp      = joDataKline.getLong("t");
//        Integer    transactionNum = joDataKline.getInt("n");
//
//        int scalePrice = priceHigh.stripTrailingZeros().scale();
//
//        BigDecimal volatility = null;
//        Long       klineId    = getKlineIdByTimestamp(timestamp);
//        BigDecimal priceAvg   = priceHigh.add(priceLow).divide(BigDecimal.valueOf(2), scalePrice, RoundingMode.HALF_DOWN);
//
//        if (priceHigh.compareTo(BigDecimal.ZERO) == 0 || priceLow.compareTo(BigDecimal.ZERO) == 0) {
//            volatility = BigDecimal.ZERO;
//        } else {
//            volatility = priceHigh.subtract(priceLow).divide(priceLow, 3, RoundingMode.HALF_DOWN);
//        }
//
//        String id = String.format("%d:%s:%s:%d",
//                                  Constants.TYPE_BINANCE_UM_PERPETUAL,
//                                  symbol,
//                                  klineInterval,
//                                  klineId
//        );
//
//        TKline kline = new TKline();
//        kline.setId(id);
//        kline.setKlineId(klineId);
//        kline.setType(Constants.TYPE_BINANCE_UM_PERPETUAL);
//        kline.setSymbol(symbol);
//        kline.setExchangeId(Constants.ExchangeBinance);
//        kline.setKlineInterval(klineInterval);
//        kline.setPriceHigh(priceHigh);
//        kline.setPriceLow(priceLow);
//        kline.setPriceOpen(priceOpen);
//        kline.setPriceClose(priceClose);
//        kline.setPriceAverage(priceAvg);
//        kline.setVolume(volume);
//        kline.setVolatility(volatility);
//        kline.setTransactionNum(transactionNum);
//        return kline;
//    }
//
//    private static synchronized long getKlineIdByTimestamp(long ts) {
//        Calendar cal = Calendar.getInstance();
//        cal.setTimeZone(TimeZone.getTimeZone("UTC"));
//        cal.setTimeInMillis(ts);
//
//        int y = cal.get(Calendar.YEAR);
//        int m = cal.get(Calendar.MONTH) + 1;
//        int d = cal.get(Calendar.DAY_OF_MONTH);
//
//        int h = cal.get(Calendar.HOUR_OF_DAY);
//        int M = cal.get(Calendar.MINUTE);
//        int s = cal.get(Calendar.SECOND);
//
//        // 2023 08 25 90 00 00
//        long value = y * 10000000000L + m * 100000000 + d * 1000000 + h * 10000 + M * 100 + s;
//
//        //log.info("klineId:{}", value);
//        return value;
//    }
//
//}
//
