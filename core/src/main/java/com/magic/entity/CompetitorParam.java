package com.magic.entity;

import lombok.Data;

/**
 * @author sevenmagicbeans
 * @date 2022/12/21
 */
@Data
public class CompetitorParam {
    private String dataSource; // 数据源，如交易所，如BINANCE等等
    private String dataType; // 数据类型，如Kline, Funding Rate等
    private String productType; // 产品类型，如现货，合约
    private String contractType; // 永续合约，交割合约
    private String margin; // 正反向(U本位，币本位)
    private String symbol; //
    private String startDateTime; // 请求的时间区间开始
    private String endDateTime; // 请求的时间区间结束
    private String granularity; // 数据粒度
    private Integer limit; // 限制的条数
    private String candleType; // k线类型，如TRADE，MIDPOINT

}
