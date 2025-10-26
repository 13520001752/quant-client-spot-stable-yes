package com.magic.service;


import cn.hutool.json.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Data
@Slf4j
public class BinanceBalance {
    private String     accountAlias;
    private String     asset;
    private BigDecimal balance;
    private BigDecimal crossWalletBalance;
    private BigDecimal crossUnPnl;
    private BigDecimal availableBalance;
    private BigDecimal maxWithdrawAmount;
    private BigDecimal marginAvailable;
    private Long       updateTime;

    public BinanceBalance(
            String accountAlias,
            String asset,
            BigDecimal balance,
            BigDecimal crossWalletBalance,
            BigDecimal crossUnPnl,
            BigDecimal availableBalance,
            BigDecimal maxWithdrawAmount,
            BigDecimal marginAvailable,
            Long updateTime
    ) {
        this.accountAlias       = accountAlias;
        this.asset              = asset;
        this.balance            = balance;
        this.crossWalletBalance = crossWalletBalance;
        this.crossUnPnl         = crossUnPnl;
        this.availableBalance   = availableBalance;
        this.maxWithdrawAmount  = maxWithdrawAmount;
        this.marginAvailable    = marginAvailable;
        this.updateTime         = updateTime;
    }

    public static BinanceBalance getFromJSONObject(JSONObject jo) {
        String     accountAlias       = jo.getStr("accountAlias");
        String     asset              = jo.getStr("asset");
        BigDecimal balance            = jo.getBigDecimal("balance");
        BigDecimal crossWalletBalance = jo.getBigDecimal("crossWalletBalance");
        BigDecimal crossUnPnl         = jo.getBigDecimal("crossUnPnl");
        BigDecimal availableBalance   = jo.getBigDecimal("availableBalance");
        BigDecimal maxWithdrawAmount  = jo.getBigDecimal("maxWithdrawAmount");
        BigDecimal marginAvailable    = jo.getBigDecimal("marginAvailable");
        Long       updateTime         = jo.getLong("updateTime");

        return new BinanceBalance(
                accountAlias,
                asset,
                balance,
                crossWalletBalance,
                crossUnPnl,
                availableBalance,
                maxWithdrawAmount,
                marginAvailable,
                updateTime
        );
    }

    public static BinanceBalance getFromMsg(String msg) {
        JSONObject jo = new JSONObject(msg);
        return getFromJSONObject(jo);
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