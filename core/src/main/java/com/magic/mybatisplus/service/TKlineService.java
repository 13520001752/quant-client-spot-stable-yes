package com.magic.mybatisplus.service;

import com.magic.mybatisplus.entity.TKline;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;


public interface TKlineService extends IService<TKline> {

    List<TKline> getLatestKlineDataBySymbol(int exchangeId, int type, String symbol, String klineInterval, int num);
}
