package com.magic.configuration;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Tom.Hardy
 * @date 29/7/23 01:46
 */

@Slf4j
@Configuration
@ConfigurationProperties("binance-price-tick-size")
@Getter
@Setter
public class BinancePriceTickSize {
    private HashMap<String, Integer> map;

//    @PostConstruct
    private void postConstruct() {
        if (map == null || map.size() == 0) {
           log.error("BinancePriceTickSize is null");
           return;
        }

        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            log.info("BinancePriceTickSize init {}:{}", entry.getKey(), entry.getValue());
        }
    }

    public synchronized int getPriceScale(String symbol) {
        if (StringUtils.isBlank(symbol)) {
            log.error("getPricePrecision failed, bad symbol:{}", symbol);
            return -1;
        }

        if (map == null || map.size() == 0) {
            log.error("getPricePrecision failed, hashmap isn't ready, symbol:{}", symbol);
            return -1;
        }

        Integer i = map.get(symbol.toUpperCase());
        if (i == null) {
            log.error("getPricePrecision failed, symbol price precision isn't configured, symbol:{}", symbol);
            return -1;
        }

        return i.intValue();
    }
}
