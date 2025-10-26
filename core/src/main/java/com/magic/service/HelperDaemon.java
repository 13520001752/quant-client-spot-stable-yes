package com.magic.service;

import cn.hutool.core.date.DateTime;
import cn.hutool.json.JSONObject;
import com.magic.constant.Constants;
import com.magic.entity.OrderIdLink;
import com.magic.entity.daemon.AveragePrice;
import com.magic.entity.daemon.MarkPrice;
import com.magic.mybatisplus.entity.TOrder;
import com.magic.utils.OkhttpUtil;
import com.magic.vo.daemon.AveragePriceGetResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;

@Slf4j
@Service
public class HelperDaemon {
    @Value("${server-url.daemon}")
    private String httpURLDaemon;

    public AveragePriceGetResponse getAveragePrice(String symbol) {
        AveragePriceGetResponse resp = new AveragePriceGetResponse();
        resp.setCode(-1);
        resp.setMessage("failed");
        resp.setAveragePrice(null);
        resp.setMarkPrice(null);

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("symbol", symbol);

        String s = null;

        try {
            s = OkhttpUtil.doGet(httpURLDaemon + "/average-price/get-24h-symbol", hashMap);
        } catch (Exception e) {
            log.info("getAveragePrice http get failed, param:{}, exception:{}, stack:{}", hashMap, e.getMessage(), e.getStackTrace());
            return null;
        }

        JSONObject jo = null;
        try {
            jo = new JSONObject(s);
        } catch (Exception e) {
            log.error("getAveragePrice parse resp failed, param:{}, exception:{}, stack:{}", hashMap, e.getMessage(), e.getStackTrace());
            return null;
        }

        AveragePrice ap = parseAveragePrice(jo);
        MarkPrice    mp = parseMarkPrice(jo);

        if (ap == null) {
            log.error("getAveragePrice failed, AveragePrice is null, param:{}", hashMap);
            return null;
        }

        if (mp == null) {
            log.error("getAveragePrice failed, MarkPrice is null, param:{}", hashMap);
            return null;
        }

        resp.setCode(jo.getInt("code"));
        resp.setMessage(jo.getStr("message"));
        resp.setAveragePrice(ap);
        resp.setMarkPrice(mp);
        return resp;
    }


    AveragePrice parseAveragePrice(JSONObject jo) {
        if (jo == null) {
            log.error("parseAveragePrice failed, jo is null");
            return null;
        }

        JSONObject joData = jo.getJSONObject("data");
        if (joData == null) {
            log.error("parseAveragePrice failed, joData is null");
            return null;
        }

        JSONObject joAveragePrice = joData.getJSONObject("average_price");
        if (joAveragePrice == null) {
            log.error("parseAveragePrice failed, joAveragePrice is null, jo:{}", jo);
            return null;
        }

        AveragePrice p = new AveragePrice();
        p.setId(joAveragePrice.getStr("id"));
        p.setExchangeId(joAveragePrice.getInt("exchangeId"));
        p.setType(joAveragePrice.getInt("type"));
        p.setSymbol(joAveragePrice.getStr("symbol"));
        p.setAveragePriceInterval(joAveragePrice.getStr("averagePriceInterval"));
        p.setAveragePrice(joAveragePrice.getBigDecimal("averagePrice"));
        p.setTsLastUpdate(joAveragePrice.getLong("tsLastUpdate"));
        p.setCreatedAt(joAveragePrice.getLocalDateTime("createdAt", null));
        p.setUpdatedAt(joAveragePrice.getLocalDateTime("updatedAt", null));
        return p;
    }

    MarkPrice parseMarkPrice(JSONObject jo) {
        if (jo == null) {
            log.error("parseMarkPrice failed, jo is null");
            return null;
        }

        JSONObject joData = jo.getJSONObject("data");
        if (joData == null) {
            log.error("parseMarkPrice failed, joData is null");
            return null;
        }

        JSONObject joMarkPrice = joData.getJSONObject("mark_price");
        if (joMarkPrice == null) {
            log.error("parseMarkPrice failed, joMarkPrice is null");
            return null;
        }

        MarkPrice p = new MarkPrice();
        p.setId(joMarkPrice.getStr("id"));
        p.setExchangeId(joMarkPrice.getInt("exchangeId"));
        p.setType(joMarkPrice.getInt("type"));
        p.setSymbol(joMarkPrice.getStr("symbol"));
        p.setPriceMark(joMarkPrice.getBigDecimal("priceMark"));
        p.setPriceSpot(joMarkPrice.getBigDecimal("priceSpot"));
        p.setTsLastUpdate(joMarkPrice.getLong("tsLastUpdate"));
        p.setCreatedAt(joMarkPrice.getLocalDateTime("createdAt", null));
        p.setUpdatedAt(joMarkPrice.getLocalDateTime("updatedAt", null));
        return p;
    }
}

