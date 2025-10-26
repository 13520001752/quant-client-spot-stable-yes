package com.magic.constant;

import com.magic.mybatisplus.entity.TSymbolConfig;

import java.math.BigDecimal;

public class Constants {
    public static Integer NUM_ZORE = 0;
    public static Integer NUM_ONE  = 1;

    public static String TRACE_ID = "tid";

    public static String ENV_TESTNET = "testnet";
    public static String ENV_PROD    = "prod";

    public static final String klineInterval01m = "1m";   // 24h 1440
    public static final String klineInterval03m = "3m";   // 480
    public static final String klineInterval05m = "5m";   // 288
    public static final String klineInterval15m = "15m";  //  96
    public static final String klineInterval30m = "30m";  //  48
    public static final String klineInterval01h = "1h";   //  24
    public static final String klineInterval1d  = "1d";

//    public static final String orderPrefix        = "892648";  // normal order
//    public static final String orderPrefixInitBuy = "883020";  // init   order

    public static int ExchangeBinance = 1;
    public static int ExchangeOKEX    = 2;

    public static String ENABLED  = "enabled";
    public static String DISABLED = "disabled";
    public static String DELETED  = "deleted";

    // symbol status
    public static String TRADING         = "TRADING";
    public static String SETTLING        = "SETTLING";
    public static String PENDING_TRADING = "PENDING_TRADING";

    public static final String QUANT_STATUS_STOPPED = "stopped";
    public static final String QUANT_STATUS_RUNNING = "running";

    public static final String LISTEN_STATUS_STOPPED  = "stopped";
    public static final String LISTEN_STATUS_STARTING = "starting";
    public static final String LISTEN_STATUS_RUNNING  = "running";
    public static final String LISTEN_STATUS_ERROR    = "error";

    public static final String QUANT_ORDER_TYPE_LIMIT  = "limit";
    public static final String QUANT_ORDER_TYPE_STOP   = "stop";
    public static final String QUANT_ORDER_TYPE_MANUAL = "manual";

    public static final String ORDER_TYPE_LIMIT                = "LIMIT";
    public static final String ORDER_TYPE_MARKET               = "MARKET";
    public static final String ORDER_TYPE_STOP                 = "STOP";
    public static final String ORDER_TYPE_STOP_MARKET          = "STOP_MARKET";
    public static final String ORDER_TYPE_STOP_MARKET_TRAILING = "TRAILING_STOP_MARKET";
    public static final String ORDER_TYPE_TAKE_PROFIT          = "TAKE_PROFIT";
    public static final String ORDER_TYPE_TAKE_PROFIT_MARKET   = "TAKE_PROFIT_MARKET";
    public static final String ORDER_TYPE_LIQUIDATION          = "LIQUIDATION";

    public static final String ORDER_STATUS_TODO             = "TODO";
    public static final String ORDER_STATUS_NEW              = "NEW";
    public static final String ORDER_STATUS_FILLED           = "FILLED";
    public static final String ORDER_STATUS_FILLED_PARTIALLY = "PARTIALLY_FILLED";
    public static final String ORDER_STATUS_CANCELED         = "CANCELED";
    public static final String ORDER_STATUS_EXPIRED          = "EXPIRED";

    public static final int DELETED_NO  = 0;
    public static final int DELETED_YES = 1;


    public static final int TYPE_BINANCE_UM_PERPETUAL       = 10101; // U本位
    public static final int TYPE_BINANCE_UM_QUARTER_CURRENT = 10102; // U本位
    public static final int TYPE_BINANCE_UM_QUARTER_NEXT    = 10103; // U本位

    public static final int TYPE_BINANCE_CM_PERPETUAL       = 10201; // C本位
    public static final int TYPE_BINANCE_CM_QUARTER_CURRENT = 10202; // C本位
    public static final int TYPE_BINANCE_CM_QUARTER_NEXT    = 10203; // C本位


    // key
    public static final String EventTypeKey     = "event_type";
    public static final String EventTypeKeyData = "data";
    public static final String EventTypeKeyTS   = "ts ";

    // value
    public static final String EventTypeOrderUpdate        = "event_order_update";
    public static final String EventTypePositionRiskUpdate = "event_position_risk_update";

    public static String fifoCmdGetkey(String apiKey) {
        return String.format("fifo:cmd:%s", apiKey);
    }

    // | 11:FDUSDUSDT | 11   | FDUSDUSDT |        4 |     1.00000000 |               0 | 2024-10-26 20:52:47 | 2024-10-26 20:52:47 |
    //| 11:USDCUSDT  | 11   | USDCUSDT  |        3 |     0.99986700 |               0 | 2023-11-28 12:46:04 | 2023-11-30 21:12:47 |
    public static final TSymbolConfig symbolConfigFDUSD_USDT = TSymbolConfig
            .builder()
            .id("11:FDUSDUSDT")
            .site("11")
            .symbol("FDUSDUSDT")
            .tickSize(4)
            .priceLatest(BigDecimal.ONE)
            .quantityPrecise(0)
            .build();

    public static final TSymbolConfig symbolConfigUSDC_USDT = TSymbolConfig
            .builder()
            .id("11:USDCUSDT")
            .site("11")
            .symbol("USDCUSDT")
            .tickSize(4)
            .priceLatest(BigDecimal.ONE)
            .quantityPrecise(0)
            .build();

    public static final TSymbolConfig symbolConfig_BTC_USDC = TSymbolConfig
            .builder()
            .id("11:BTCUSDC")
            .site("11")
            .symbol("BTCUSDC")
            .tickSize(4)
            .priceLatest(BigDecimal.ONE)
            .quantityPrecise(0)
            .build();


    public static TSymbolConfig getSymbolConfig(String symbol) {
        switch (symbol) {
            case "FDUSDUSDT":
                return symbolConfigFDUSD_USDT;
            case "USDCUSDT":
                return symbolConfigUSDC_USDT;
            case "BTCUSDC":
                return symbolConfig_BTC_USDC;
            default:
                return null;
        }

    }
}
