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
@TableName("t_exchange_info_asset")
@ApiModel(value = "TExchangeInfoAsset对象", description = "")
public class TExchangeInfoAsset implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("ID")
    @TableId("id")
    private String id;

    @ApiModelProperty("交易所名称")
    @TableField("exchange_id")
    private Integer exchangeId;

    @ApiModelProperty("保证金USDT")
    @TableField("asset")
    private String asset;

    @ApiModelProperty("是否可用作保证金")
    @TableField("margin_available")
    private Boolean marginAvailable;

    @ApiModelProperty("保证金资产自动兑换阈值")
    @TableField("auto_asset_exchange")
    private Integer autoAssetExchange;

    @ApiModelProperty("创建时间")
    @TableField("created_at")
    private LocalDateTime createdAt;

    @ApiModelProperty("修改时间")
    @TableField("updated_at")
    private LocalDateTime updatedAt;


}
