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
@TableName("t_average_price")
@ApiModel(value = "TAveragePrice对象", description = "")
public class TAveragePrice implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableField("id")
    private String id;

    @TableField("exchange_id")
    private Integer exchangeId;

    @TableField("type")
    private Integer type;

    @TableField("symbol")
    private String symbol;

    @TableField("average_price_interval")
    private String averagePriceInterval;

    @TableField("average_price")
    private BigDecimal averagePrice;

    @TableField("ts_last_update")
    private Long tsLastUpdate;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;


}
