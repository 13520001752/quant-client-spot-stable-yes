package com.magic.service;


import cn.hutool.json.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;


@Data
@Slf4j
public class BinancePriceMark {
    private String     symbol;                  // 交易对
    private BigDecimal priceMark;               // 标记价格
    private BigDecimal priceIndex;              // 现货指数价格
    private BigDecimal priceSettleEstimated;    // 预估结算价,仅在结算前最后一小时有参考价值
    private BigDecimal fundingRate;             // 资金费率
    private Long       tsFundingNext;           // 下个资金时间

    public BinancePriceMark(
            String symbol,
            BigDecimal priceMark,
            BigDecimal priceIndex,
            BigDecimal priceSettleEstimated,
            BigDecimal fundingRate,
            Long tsFundingNext
    ) {
        this.symbol               = symbol;
        this.priceMark            = priceMark;
        this.priceIndex           = priceIndex;
        this.priceSettleEstimated = priceSettleEstimated;
        this.fundingRate          = fundingRate;
        this.tsFundingNext        = tsFundingNext;
    }

    public static BinancePriceMark getFromJSONObject(JSONObject jo) {
        //[
//        {
//        "e": "markPriceUpdate",     // 事件类型
//        "E": 1562305380000,         // 事件时间
//        "s": "BTCUSDT",             // 交易对
//        "p": "11185.87786614",      // 标记价格
//        "i": "11784.62659091"       // 现货指数价格
//        "P": "11784.25641265",      // 预估结算价,仅在结算前最后一小时有参考价值
//        "r": "0.00030000",          // 资金费率
//        "T": 1562306400000          // 下个资金时间
//        }
//]

        String     symbol               = jo.getStr("s");         // 交易对
        BigDecimal priceMark            = jo.getBigDecimal("p");  // 标记价格
        BigDecimal priceIndex           = jo.getBigDecimal("i");  // 现货指数价格
        BigDecimal priceSettleEstimated = jo.getBigDecimal("P");  // 预估结算价,仅在结算前最后一小时有参考价值
        BigDecimal fundingRate          = jo.getBigDecimal("r");  // 资金费率
        Long       tsFundingNext        = jo.getLong("T");  // 下个资金时间

        return new BinancePriceMark(
                symbol,
                priceMark,
                priceIndex,
                priceSettleEstimated,
                fundingRate,
                tsFundingNext
        );
    }

    public static BinancePriceMark getFromMsg(String msg) {
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