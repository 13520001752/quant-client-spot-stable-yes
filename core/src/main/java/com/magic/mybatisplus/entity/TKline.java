package com.magic.mybatisplus.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author magic beans
 * @since 2023-08-25
 */
@Getter
@Setter
@TableName("t_kline")
@ApiModel(value = "TKline对象", description = "")
public class TKline implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("exchangeId-symbol-interval-ts")
    @TableField("id")
    private String id;

    @TableField("exchange_id")
    private Integer exchangeId;

    @ApiModelProperty("um, cm, spot...")
    @TableField("type")
    private Integer type;

    @TableField("symbol")
    private String symbol;

    @TableField("kline_id")
    private Long klineId;

    @TableField("kline_interval")
    private String klineInterval;

    @TableField("price_high")
    private BigDecimal priceHigh;

    @TableField("price_low")
    private BigDecimal priceLow;

    @TableField("price_open")
    private BigDecimal priceOpen;

    @TableField("price_close")
    private BigDecimal priceClose;

    @TableField("price_average")
    private BigDecimal priceAverage;

    @TableField("volatility")
    private BigDecimal volatility;

    @ApiModelProperty("成交量")
    @TableField("volume")
    private BigDecimal volume;

    @ApiModelProperty("交易笔数")
    @TableField("transaction_num")
    private Integer transactionNum;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;


}
