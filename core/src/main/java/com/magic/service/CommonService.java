package com.magic.service;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.binance.connector.client.SpotClient;
import com.binance.connector.client.impl.SpotClientImpl;
import com.magic.constant.Constants;
import com.magic.mybatisplus.entity.TConfig;
import com.magic.mybatisplus.entity.TSymbolConfig;
import com.magic.mybatisplus.mapper.TConfigMapper;
import com.magic.mybatisplus.mapper.TSymbolConfigMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class CommonService {
    @Autowired
    TConfigMapper       configMapper;
    @Autowired
    TSymbolConfigMapper symbolConfigMapper;

    // 共用一个
    public SpotClient clientHTTP = new SpotClientImpl();

    ConcurrentHashMap<Integer, SpotClient> hashMapClientAccountHTTP = new ConcurrentHashMap<>();

    public synchronized SpotClient getClientHTTP() {
        return clientHTTP;
    }

    public synchronized SpotClient getClientAccountHTTP(Integer configId) {
        SpotClient clientAccountHTTP = hashMapClientAccountHTTP.get(configId);
        if (clientAccountHTTP != null) {
            return clientAccountHTTP;
        }

        TConfig config = getConfig(configId);
        if (config == null) {
            log.info("getClientAccountHTTP failed, config isn't exist, id:{}", configId);
            return null;
        }

        TSymbolConfig symbolConfig = Constants.getSymbolConfig(config.getSymbol());
        if (symbolConfig == null) {
            log.info("getClientAccountHTTP failed, configSymbol isn't exist, id:{}, symbol:{}", configId, config.getSymbol());
            return null;
        }

        clientAccountHTTP = new SpotClientImpl(config.getApiKey(), config.getApiSecret());
        hashMapClientAccountHTTP.put(configId, clientAccountHTTP);
        return clientAccountHTTP;
    }


    public synchronized TConfig getConfig(Integer configId) {
        TConfig config = configMapper.selectById(configId);
        if (config == null) {
            log.info("getConfig failed, config isn't exist, id:{}", configId);
            return null;
        }
        return config;
    }

//    public synchronized TSymbolConfig getSymbolConfig(Integer configId, String symbol) {
//        TSymbolConfig symbolConfig = new LambdaQueryChainWrapper<>(symbolConfigMapper)
//                .eq(TSymbolConfig::getSymbol, symbol.toUpperCase())
//                .one();

//        TSymbolConfig symbolConfig = Constants.getSymbolConfig(symbol);
//
//        if (symbolConfig == null) {
//            log.info("getSymbolConfig failed, symbol config isn't exist, id:{}", configId);
//            return null;
//        }
//        return symbolConfig;
//    }

}

