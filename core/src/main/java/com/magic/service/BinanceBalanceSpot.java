package com.magic.service;


import cn.hutool.json.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Data
@Slf4j
public class BinanceBalanceSpot {
    private String     asset;
    private BigDecimal free;
    private BigDecimal locked;
    private long       updateTime;

    public BinanceBalanceSpot(
            String asset,
            BigDecimal free,
            BigDecimal locked,
            Long updateTime
    ) {
        this.asset  = asset;
        this.free   = free;
        this.locked = locked;

        if (this.free == null) {
            this.free = BigDecimal.ZERO;
        }

        if (this.locked == null) {
            this.locked = BigDecimal.ZERO;
        }

        if (updateTime != null) {
            this.updateTime = updateTime;
        } else {
            this.updateTime = System.currentTimeMillis();
        }
    }

    public static BinanceBalanceSpot getFromJSONObject(JSONObject jo, long ts) {
        // {
        //  "e": "outboundAccountPosition", // 事件类型
        //  "E": 1564034571105,             // 事件时间
        //  "u": 1564034571073,             // 账户末次更新时间戳
        //  "B": [                          // 余额
        //    {
        //      "a": "ETH",                 // 资产名称
        //      "f": "10000.000000",        // 可用余额
        //      "l": "0.000000"             // 冻结余额
        //    }
        //  ]
        //}

        String     asset      = jo.getStr("a");
        BigDecimal free       = jo.getBigDecimal("wb");
        BigDecimal locked     = jo.getBigDecimal("cw");
        Long       updateTime = ts;

        return new BinanceBalanceSpot(
                asset,
                free,
                locked,
                ts
        );
    }
}

