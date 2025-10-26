package com.magic.mybatisplus.entity;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Data
public class TExchangeInfo {
    public ConcurrentHashMap<String, TExchangeInfoAsset>  hashMapAsset  = new ConcurrentHashMap<>();
    public ConcurrentHashMap<String, TExchangeInfoSymbol> hashMapSymbol = new ConcurrentHashMap<>();
    public ConcurrentHashMap<String, TExchangeInfoRateLimit> hashMapRateLimit = new ConcurrentHashMap<>();

    public TExchangeInfo(
            ConcurrentHashMap<String, TExchangeInfoAsset> hashMapAsset,
            ConcurrentHashMap<String, TExchangeInfoSymbol> hashMapSymbol,
            ConcurrentHashMap<String, TExchangeInfoRateLimit> hashMapRateLimit) {
        this.hashMapSymbol    = hashMapSymbol;
        this.hashMapAsset     = hashMapAsset;
        this.hashMapRateLimit = hashMapRateLimit;
    }
}