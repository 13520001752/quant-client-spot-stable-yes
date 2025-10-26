//package com.magic.task;
//
//import cn.hutool.core.date.DateUtil;
//import cn.hutool.json.JSONArray;
//import cn.hutool.json.JSONObject;
//import com.binance.connector.futures.client.exceptions.BinanceClientException;
//import com.binance.connector.futures.client.exceptions.BinanceConnectorException;
//import com.binance.connector.futures.client.impl.UMFuturesClientImpl;
//import com.binance.connector.futures.client.impl.UMWebsocketClientImpl;
//import com.magic.service.BinanceBalance;
//import com.magic.service.HelperBinanceLeverageActual;
//import com.magic.service.QuantService;
//import com.sun.jdi.connect.spi.TransportService;
//import io.micrometer.core.instrument.util.StringUtils;
//import lombok.extern.slf4j.Slf4j;
//import org.bouncycastle.jcajce.provider.digest.Blake2b;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.batch.JobLauncherApplicationRunner;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.Resource;
//import javax.swing.plaf.basic.BasicLookAndFeel;
//import java.math.BigDecimal;
//import java.util.LinkedHashMap;
//
//@Component
//@Slf4j
//public class TestTask {
//    @Autowired
//    QuantService quantService;
//
////    @Scheduled(fixedRate = 3000 * 10)
//    public void TaskAccountSub() {
////        BigDecimal leverageActual = HelperBinanceLeverageActual.getLeverageActual(
////                quantService.hashMapBalance2,
////                quantService.hashMapPosition,
////                quantService.symbol,
////                "USDT"
////        );
////        log.info("");
////        log.info("TaskAccountSub       LeverageOpen:{}", quantService.leverageMaxConfig);
////        log.info("TaskAccountSub LeverageMaxAllowed:{}", quantService.leverageMaxAllowed);
////        log.info("TaskAccountSub     LeverageActual:{}", leverageActual.toPlainString());
////        log.info("");
//    }
//}
