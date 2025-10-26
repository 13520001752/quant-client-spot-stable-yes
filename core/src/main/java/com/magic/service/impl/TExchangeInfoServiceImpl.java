package com.magic.service.impl;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.magic.mybatisplus.entity.TExchangeInfo;
import com.magic.mybatisplus.entity.TExchangeInfoAsset;
import com.magic.mybatisplus.entity.TExchangeInfoRateLimit;
import com.magic.mybatisplus.entity.TExchangeInfoSymbol;
import com.magic.mybatisplus.mapper.TExchangeInfoAssetMapper;
import com.magic.mybatisplus.mapper.TExchangeInfoRateLimitMapper;
import com.magic.mybatisplus.mapper.TExchangeInfoSymbolMapper;
import com.magic.mybatisplus.service.TExchangeInfoAssetService;
import com.magic.mybatisplus.service.TExchangeInfoRateLimitService;
import com.magic.mybatisplus.service.TExchangeInfoSymbolService;
import com.magic.service.TExchangeInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class TExchangeInfoServiceImpl implements TExchangeInfoService {
    @Autowired
    TExchangeInfoAssetService     assetService;
    @Autowired
    TExchangeInfoRateLimitService rateLimitService;
    @Autowired
    TExchangeInfoSymbolService    symbolService;
    @Autowired
    TExchangeInfoAssetMapper      exchangeInfoAssetMapper;
    @Autowired
    TExchangeInfoSymbolMapper     exchangeInfoSymbolMapper;
    @Autowired
    TExchangeInfoRateLimitMapper  exchangeInfoRateLimitMapper;

    @Override
    public TExchangeInfo saveOrUpdate(TExchangeInfo info) {
        ConcurrentHashMap<String, TExchangeInfoAsset>     hashMapAsset     = info.hashMapAsset;
        ConcurrentHashMap<String, TExchangeInfoSymbol>    hashMapSymbol    = info.hashMapSymbol;
        ConcurrentHashMap<String, TExchangeInfoRateLimit> hashMapRateLimit = info.hashMapRateLimit;

        if (hashMapAsset != null && !hashMapAsset.isEmpty()) {
            hashMapAsset.forEach((key, value) -> {
                assetService.saveOrUpdate(value);
            });
        }

        if (hashMapSymbol != null && !hashMapSymbol.isEmpty()) {
            hashMapSymbol.forEach((key, value) -> {
                symbolService.saveOrUpdate(value);
            });
        }

        if (hashMapRateLimit != null && !hashMapRateLimit.isEmpty()) {
            hashMapRateLimit.forEach((key, value) -> {
                rateLimitService.saveOrUpdate(value);
            });
        }
        return null;
    }
}