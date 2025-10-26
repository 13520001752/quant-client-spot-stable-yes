package com.magic.service;

import cn.hutool.json.JSONObject;
import com.magic.mybatisplus.entity.TConfig;
import com.magic.mybatisplus.entity.TSymbolConfig;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Slf4j
public class HelperBinanceAccountInfo {
    private TConfig       config;
    private TSymbolConfig symbolConfig;

    public HelperBinanceAccountInfo(TConfig config, TSymbolConfig symbolConfig) {
        this.config       = config;
        this.symbolConfig = symbolConfig;
    }

    public BinanceBalanceSpot handleAsset(JSONObject jo) {
        String     coin               = jo.getStr("asset");
        BigDecimal walletBalance      = jo.getBigDecimal("availableBalance");
        BigDecimal walletBalanceCross = jo.getBigDecimal("crossWalletBalance");
        Long       ts                 = jo.getLong("updateTime");

        BinanceBalanceSpot b = new BinanceBalanceSpot(coin, walletBalance, walletBalanceCross, ts);
        return b;
    }



}
