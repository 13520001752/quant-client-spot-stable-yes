package com.magic.mybatisplus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.magic.mybatisplus.entity.TKline;
import com.magic.mybatisplus.entity.TOrder;
import com.magic.mybatisplus.mapper.TKlineMapper;
import com.magic.mybatisplus.service.TKlineService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TKlineServiceImpl extends ServiceImpl<TKlineMapper, TKline> implements TKlineService {

    @Override
    public List<TKline> getLatestKlineDataBySymbol(int exchangeId, int type, String symbol, String klineInterval, int num) {
        LambdaQueryWrapper<TKline> query = new LambdaQueryWrapper<>();

        query.eq(TKline::getExchangeId, exchangeId);
        query.eq(TKline::getType, type);
        query.eq(TKline::getSymbol, symbol);
        query.eq(TKline::getKlineInterval, klineInterval);
        query.orderByDesc(TKline::getKlineId);
        query.last("limit " + num);

        return this.list(query);
    }
}
