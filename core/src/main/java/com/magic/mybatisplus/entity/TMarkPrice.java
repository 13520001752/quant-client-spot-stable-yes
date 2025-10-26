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
@TableName("t_mark_price")
@ApiModel(value = "TMarkPrice对象", description = "")
public class TMarkPrice implements Serializable {

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

    @ApiModelProperty("标记价格")
    @TableField("price_mark")
    private BigDecimal priceMark;

    @ApiModelProperty("现货指数价格")
    @TableField("price_spot")
    private BigDecimal priceSpot;

    @ApiModelProperty("最后一次更新时间")
    @TableField("ts_last_update")
    private Long tsLastUpdate;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;


}
