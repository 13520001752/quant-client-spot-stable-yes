package com.magic.service;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONException;
import cn.hutool.json.JSONObject;
import com.magic.constant.Constants;
import com.magic.mybatisplus.entity.TKline;
import com.magic.mybatisplus.entity.TMarkPrice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

@Slf4j
@Service
public class HelperMarkPrice {
    public static TMarkPrice parseFromHttp(int exchangeId, JSONObject jo) {
        return null;
    }

    public static List<TMarkPrice> parseFromWebSocket(String msg) {
        JSONArray ja = null;
        try {
            ja = new JSONArray(msg);
        } catch (JSONException e) {
            log.error("parseFromWebSocket failed, msg:{}, error:{}, stack:{}", msg, e.getMessage(), e.getStackTrace());
            return null;
        }

        if (ja.isEmpty()) {
            return null;
        }

        JSONObject            jo            = null;
        TMarkPrice            markPrice     = null;
        ArrayList<TMarkPrice> markPriceList = new ArrayList<>();

        for (int i = 0; i < ja.size(); ++i) {
            jo = ja.getJSONObject(i);

            markPrice = parse(jo);
            if (markPrice == null) {
                continue;
            }

            markPriceList.add(markPrice);
        }

        return markPriceList;
    }

    public static TMarkPrice parse(JSONObject jo) {
        //    [
        //        {
        //                "e": "markPriceUpdate",     // 事件类型
        //                "E": 1562305380000,         // 事件时间
        //                "s": "BTCUSDT",             // 交易对

        //                "p": "11185.87786614",      // 标记价格
        //                "i": "11784.62659091"       // 现货指数价格
        //                "P": "11784.25641265",      // 预估结算价,仅在结算前最后一小时有参考价值
        //                "r": "0.00030000",          // 资金费率
        //                "T": 1562306400000          // 下个资金时间
        //        }
        //    ]

        String     event          = jo.getStr("e");
        Long       eventTimestamp = jo.getLong("E");
        String     symbol         = jo.getStr("s");
        BigDecimal priceMark      = jo.getBigDecimal("p");
        BigDecimal priceSpot      = jo.getBigDecimal("i");

        if (!"markPriceUpdate".equalsIgnoreCase(event)) {
            return null;
        }

        String id = String.format("%d:%s:%s",
                                  Constants.ExchangeBinance,
                                  Constants.TYPE_BINANCE_UM_PERPETUAL,
                                  symbol
        );

        TMarkPrice p = new TMarkPrice();
        p.setId(id);
        p.setExchangeId(Constants.ExchangeBinance);
        p.setType(Constants.TYPE_BINANCE_UM_PERPETUAL);
        p.setSymbol(symbol);
        p.setPriceMark(priceMark);
        p.setPriceSpot(priceSpot);
        p.setTsLastUpdate(eventTimestamp);

        return p;
    }
}

