package com.magic.entity;

import cn.hutool.json.JSONUtil;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collection;

/**
 * @author sevenmagicbeans
 * @date 2022/12/21
 */
@Data
public class CompetitorParamBatch {
    private final Collection<String> symbols;
    private final String dataSource; // 数据源，如交易所，如BINANCE等等
    private final String dataType; // 数据类型，如Kline, Funding Rate等
    private final String productType; // 产品类型，如现货，合约
    private final String contractType; // 永续合约，交割合约
    private final String margin; // 正反向(U本位，币本位)
    private final String startDateTime; // 请求的时间区间开始
    private final String endDateTime; // 请求的时间区间结束
    private final String granularity; // 数据粒度
    private final Integer limit; // 限制的条数
    private final String candleType; // k线类型，如TRADE，MIDPOINT


    public LocalDateTime startLocalDateTime() {
        return LocalDateTime.parse(startDateTime);
    }


    public LocalDateTime endLocalDateTime() {
        return LocalDateTime.parse(endDateTime);
    }


    public LocalDate startLocalDate() {
        return startLocalDateTime().toLocalDate();
    }


    public LocalDate endLocalDate() {
        return endLocalDateTime().toLocalDate();
    }


    public String startDateString() {
        return startLocalDate().toString();
    }


    public String endDateString() {
        return endLocalDate().toString();
    }
    /**
     * string转timestamp(秒)
     */
    public String startSecondTimestamp() {
        return String.valueOf(LocalDateTime.parse(startDateTime).toEpochSecond(ZoneOffset.of("+8")));
    }
    /**
     * string转timestamp(秒)
     */
    public String endSecondTimestamp() {
        return String.valueOf(LocalDateTime.parse(endDateTime).toEpochSecond(ZoneOffset.of("+8")));
    }
    /**
     * string转timestamp(秒)
     */
    public String startMilliSecondTimestamp() {
        return String.valueOf(LocalDateTime.parse(startDateTime).toInstant(ZoneOffset.of("+8")).toEpochMilli());
    }
    /**
     * string转timestamp(秒)
     */
    public String endMilliSecondTimestamp() {
        return String.valueOf(LocalDateTime.parse(endDateTime).toInstant(ZoneOffset.of("+8")).toEpochMilli());
    }


    @Override
    public String toString() {
        return JSONUtil.toJsonStr(this);
    }
}
