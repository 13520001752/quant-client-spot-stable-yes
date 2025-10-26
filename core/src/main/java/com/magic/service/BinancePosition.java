package com.magic.service;


import cn.hutool.json.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
@Slf4j
public class BinancePosition {
    private String     symbol;              // 交易对
    private BigDecimal amountPosition;      // 仓位
    private BigDecimal priceEntry;          // 入仓价格
    private BigDecimal accumulatedRealized; // (费前)累计实现损益
    private BigDecimal unrealizedPnL;       // 持仓未实现盈亏
    private String     marginType;          // 保证金模式
    private BigDecimal isolatedWallet;      // 若为逐仓，仓位保证金; Isolated Wallet (if isolated position)
    private String     positionIndex;       // 持仓方向
    private Long       updateTime;

    private BigDecimal priceMark;       // 传入
    private BigDecimal positionValue;   // 持仓总价值：计算得出
    private int        positionValuePrintTimes = 10;

    public BinancePosition(
            String symbol,
            BigDecimal amountPosition,
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

    // websocket订单/余额变动推送
    public static BinancePosition getFromWebSocketJSONObject(JSONObject jo, Long ts) {
        String     symbol              = jo.getStr("s");          // 交易对
        BigDecimal amountPosition      = jo.getBigDecimal("pa");         // 仓位
        String     marginType          = jo.getStr("mt");  // 保证金模式
        String     positionIndex       = jo.getStr("ps");  // 持仓方向;
        BigDecimal priceEntry          = jo.getBigDecimal("ep");  // 入仓价格
        BigDecimal accumulatedRealized = jo.getBigDecimal("cr");  // (费前)累计实现损益
        BigDecimal unrealizedPnL       = jo.getBigDecimal("up");  // 持仓未实现盈亏
        BigDecimal isolatedWallet      = jo.getBigDecimal("iw");  // 若为逐仓，仓位保证金

        return new BinancePosition(
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

    // 接口查询
    public static BinancePosition getFromHttpJSONObject(JSONObject jo, Long ts) {
        String     symbol              = jo.getStr("s");          // 交易对
        BigDecimal amountPosition      = jo.getBigDecimal("pa");         // 仓位
        String     marginType          = jo.getStr("mt");  // 保证金模式
        String     positionIndex       = jo.getStr("ps");  // 持仓方向;
        BigDecimal priceEntry          = jo.getBigDecimal("ep");  // 入仓价格
        BigDecimal accumulatedRealized = jo.getBigDecimal("cr");  // (费前)累计实现损益
        BigDecimal unrealizedPnL       = jo.getBigDecimal("up");  // 持仓未实现盈亏
        BigDecimal isolatedWallet      = jo.getBigDecimal("iw");  // 若为逐仓，仓位保证金

        return new BinancePosition(
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
        BigDecimal pv = this.amountPosition;
        pv = pv.multiply(markPrice).setScale(8, RoundingMode.HALF_DOWN);

        this.priceMark     = markPrice;
        this.positionValue = pv.setScale(1, RoundingMode.HALF_DOWN);

        if (pv.compareTo(BigDecimal.ZERO) == 1) {
            if (++positionValuePrintTimes % 10 == 0) {
                positionValuePrintTimes = 0;
                log.info("PositionValue:{}, markPrice:{}, positionAmount:{}", pv.toPlainString(), markPrice.toPlainString(), this.amountPosition);
            }
        }
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