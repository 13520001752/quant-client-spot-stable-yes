package com.magic.service;


import cn.hutool.json.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Data
@Slf4j
// ACCOUNT_UPDATE中的余额信息
public class BinanceBalanceHttp {
    private String     coin;
    private BigDecimal walletBalance;           // 钱包余额
    private BigDecimal walletBalanceCross;      // 除去逐仓仓位保证金的钱包余额
    private Long       updateTime;

    public BinanceBalanceHttp(
            String coin,
            BigDecimal walletBalance,
            BigDecimal walletBalanceCross,
            Long updateTime
    ) {
        this.coin               = coin;
        this.walletBalance      = walletBalance;
        this.walletBalanceCross = walletBalanceCross;
        this.updateTime         = updateTime;
    }

    public static BinanceBalanceHttp getFromJSONObject(JSONObject jo, long ts) {
//        [{
//                "a":"USDT",                   // 资产名称
//                "wb":"122624.12345678",       // 钱包余额
//                "cw":"100.12345678",          // 除去逐仓仓位保证金的钱包余额
//                "bc":"50.12345678"            // 除去盈亏与交易手续费以外的钱包余额改变量
//        }]

        String     coin               = jo.getStr("a");
        BigDecimal walletBalance      = jo.getBigDecimal("wb");
        BigDecimal walletBalanceCross = jo.getBigDecimal("cw");
        Long       updateTime         = ts;

        return new BinanceBalanceHttp(
                coin,
                walletBalance,
                walletBalanceCross,
                updateTime
        );
    }
}

//  {
//        "accountAlias":"SgmYSguXSgTifWXq",
//        "asset":"USDT",
//        "balance":"999.95982308",
//        "crossWalletBalance":"999.95982308",
//        "crossUnPnl":"0.00000000",
//        "availableBalance":"999.95982308",
//        "maxWithdrawAmount":"999.95982308",
//        "marginAvailable":true,
//        "updateTime":1688220726195
//   }