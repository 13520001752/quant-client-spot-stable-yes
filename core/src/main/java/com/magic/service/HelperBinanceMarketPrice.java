package com.magic.service;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONException;
import cn.hutool.json.JSONObject;
import com.binance.connector.client.SpotClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class HelperBinanceMarketPrice {
    public int handle(
            ConcurrentHashMap<String, BinancePriceMark> hashMapPrice,
            ConcurrentHashMap<String, BinancePosition> hashMapPosition,
            String msg) {
        JSONArray ja = null;
        try {
            ja = new JSONArray(msg);
        } catch (JSONException e) {
            log.error("HelperBinanceMarketPrice exception:{}, msg:{}", e.getMessage(), msg);
            e.printStackTrace();
            return -1;
        }

        if (ja == null || ja.size() == 0) {
            return 0;
        }

        JSONObject       jo        = null;
        String           symbol    = null;
        BinancePriceMark priceMark = null;

        for (int i = 0; i < ja.size(); ++i) {
            jo        = ja.getJSONObject(i);
            priceMark = BinancePriceMark.getFromJSONObject(jo);
            symbol    = priceMark.getSymbol();

            hashMapPrice.put(symbol, priceMark);

            // 更新持仓价值
            {
                BinancePosition position = hashMapPosition.get(symbol);
                if (position != null) {
                    position.setPrice(priceMark.getPriceMark());
                }
            }
        }
        return 0;
    }

    public static Pair<Integer, BigDecimal> getMarketPrice(SpotClient clientAccountHTTP, String symbol) {
        Pair<Integer, BigDecimal> r           = null;
        int                       retCode     = 0;
        BigDecimal                priceMarket = null;
        int                       triedTimes  = 0;

        // 查询最新价格
        while (triedTimes <= 5) {
            r           = QuantUtil.getPriceLatest(clientAccountHTTP, symbol);
            retCode     = r.getLeft();
            priceMarket = r.getRight();

            if (retCode != 0) {
                triedTimes++;
                log.error("getMarketPrice failed, symbol:{}, tried:{}", symbol, triedTimes);
                continue;
            }
            break;
        }

        if (priceMarket == null) {
            log.info("getMarketPrice failed, symbol:{}, triedTimes:{}", symbol, triedTimes);
            return Pair.of(-1, null);
        }

        log.info("getMarketPrice priceMarket:{}, symbol:{}, triedTimes:{}", priceMarket, symbol, triedTimes);
        return Pair.of(0, priceMarket);
    }
}

