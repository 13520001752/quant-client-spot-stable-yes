//package com.magic.service;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.util.LinkedHashMap;
//
//@Slf4j
//@Service
//public class QuantTask2 extends Thread {
//    @Value("{quant1.symbol}")
//    private String symbol;
//
//    @Value("{quant1.leverage-open}")
//    private String leverageOpen1;
//
//    @Value("{quant1.leverage-max-actual}")
//    private String leverageMaxActual1;
//
//    @Value("{quant1.amount-usdt}")
//    private String amountUSDT1;
//
//    @Value("{quant1.amount-usdt-max}")
//    private String amountUSDTMax1;
//
//    @Value("{quant1.trigger-open}")
//    private String triggerOpen1;
//
//    @Value("{quant1.trigger-close}")
//    private String triggerClose1;
//
//    @Autowired
//    private QuantService quantService;
//
//
//    @Override
//    public void run() {
//        init();
//
//
//    }
//
//    public int init() {
//        int        leverageOpen      = Integer.parseInt(leverageOpen1);
//        int        leverageMaxActual = Integer.parseInt(leverageMaxActual1);
//        BigDecimal amountUSDT        = new BigDecimal(amountUSDT1);
//        BigDecimal amountUSDTMax     = new BigDecimal(amountUSDTMax1);
//        int        triggerOpen       = Integer.parseInt(triggerOpen1);
//        int        triggerClose      = Integer.parseInt(triggerClose1);
//
//        log.info("leverageOpen:{}", leverageOpen);
//        log.info("leverageMaxActual:{}", leverageMaxActual);
//        log.info("amountUSDT:{}", amountUSDT);
//        log.info("amountUSDTMax:{}", amountUSDTMax);
//        log.info("triggerOpen:{}", triggerOpen);
//        log.info("triggerClose:{}", triggerClose);
//
//        switch (symbol.toLowerCase()) {
//            case "ethusdt":
//            case "btcusdt":
//                break;
//            default:
//                log.error("QuantTask bad symbol:{}", symbol);
//                return -1;
//        }
//
//        // 设置杠杆
//        if (leverageOpen <= 0 || leverageOpen >= 20) {
//            log.error("QuantTask bad leverageOpen:{}", leverageOpen);
//            return -2;
//        }
//
//        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
//        parameters.put("symbol", symbol);
//        parameters.put("leverage", leverageOpen);
//
//        String s = quantService.clientAccountHTTP.account().changeInitialLeverage(parameters);
//        log.info("s:{}", s);
//
//
//        return 0;
//    }
//
//}
