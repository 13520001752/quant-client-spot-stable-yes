package com.magic.mybatisplus.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
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
 * @since 2023-08-29
 */
@Getter
@Setter
@TableName("t_exchange_info_rate_limit")
@ApiModel(value = "TExchangeInfoRateLimit对象", description = "")
public class TExchangeInfoRateLimit implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("id")
    private String id;

    @TableField("exchange_id")
    private Integer exchangeId;

    @TableField("interval_name")
    private String intervalName;

    @TableField("interval_num")
    private Integer intervalNum;

    @TableField("limit_num")
    private Integer limitNum;

    @ApiModelProperty("REQUEST_WEIGHT, ORDERS")
    @TableField("rate_limit_type")
    private String rateLimitType;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;


}
