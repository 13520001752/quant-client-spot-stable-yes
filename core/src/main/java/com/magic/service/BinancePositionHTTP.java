package com.magic.service;


import cn.hutool.json.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
@Slf4j
public class BinancePositionHTTP {
    private String     symbol;              // 交易对
    private String     amountPosition;      // 仓位
    private BigDecimal priceEntry;          // 入仓价格
    private BigDecimal accumulatedRealized; // (费前)累计实现损益
    private BigDecimal unrealizedPnL;       // 持仓未实现盈亏
    private String     marginType;          // 保证金模式
    private BigDecimal isolatedWallet;      // 若为逐仓，仓位保证金; Isolated Wallet (if isolated position)
    private String     positionIndex;       // 持仓方向
    private Long       updateTime;

    private BigDecimal priceMark;       // 传入
    private BigDecimal positionValue;   // 计算得出

    public BinancePositionHTTP(
            String symbol,
            String amountPosition,
            BigDecimal priceEntry,
            BigDecimal accumulatedRealized,
            BigDecimal unrealizedPnL,
            String marginType,
            BigDecimal isolatedWallet,
            String positionIndex,
            Long updateTime
    ) {
        this.symbol              = symbol;
        this.amountPosition      = amountPosition;
        this.priceEntry          = priceEntry;
        this.accumulatedRealized = accumulatedRealized;
        this.unrealizedPnL       = unrealizedPnL;
        this.marginType          = marginType;
        this.isolatedWallet      = isolatedWallet;
        this.positionIndex       = positionIndex;
        this.updateTime          = updateTime;
    }

    public static BinancePositionHTTP getFromJSONObject(JSONObject jo, Long ts) {
        String     symbol              = jo.getStr("s");          // 交易对
        String     amountPosition      = jo.getStr("pa");         // 仓位
        String     marginType          = jo.getStr("mt");  // 保证金模式
        String     positionIndex       = jo.getStr("ps");  // 持仓方向;
        BigDecimal priceEntry          = jo.getBigDecimal("ep");  // 入仓价格
        BigDecimal accumulatedRealized = jo.getBigDecimal("cr");  // (费前)累计实现损益
        BigDecimal unrealizedPnL       = jo.getBigDecimal("up");  // 持仓未实现盈亏
        BigDecimal isolatedWallet      = jo.getBigDecimal("iw");  // 若为逐仓，仓位保证金

        return new BinancePositionHTTP(
                symbol,
                amountPosition,
                priceEntry,
                accumulatedRealized,
                unrealizedPnL,
                marginType,
                isolatedWallet,
                positionIndex,
                ts
        );
    }

    public int setPrice(BigDecimal markPrice) {
        BigDecimal pv = new BigDecimal(this.amountPosition);
        pv = pv.multiply(markPrice).setScale(8, RoundingMode.HALF_DOWN);

        this.priceMark     = markPrice;
        this.positionValue = pv;
        log.info("PositionValue:{}, markPrice:{}, positionAmount:{}", pv, markPrice, this.amountPosition);
        return 0;
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