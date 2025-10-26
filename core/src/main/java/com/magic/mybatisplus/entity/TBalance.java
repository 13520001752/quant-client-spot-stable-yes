package com.magic.mybatisplus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
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
 * HTTP接口，账户信息V2 (USER_DATA)
 * </p>
 *
 * @author magic beans
 * @since 2023-08-13
 */
@Getter
@Setter
@TableName("t_balance")
@ApiModel(value = "TBalance对象", description = "HTTP接口，账户信息V2 (USER_DATA)")
public class TBalance implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("api_key_id")
    private Long apiKeyId;

    @ApiModelProperty("币种")
    @TableField("asset")
    private String asset;

    @TableField("wallet_balance")
    private BigDecimal walletBalance;

    @TableField("unrealized_profit")
    private BigDecimal unrealizedProfit;

    @TableField("margin_balance")
    private BigDecimal marginBalance;

    @TableField("maint_margin")
    private BigDecimal maintMargin;

    @TableField("initial_margin")
    private BigDecimal initialMargin;

    @TableField("position_Initial_margin")
    private BigDecimal positionInitialMargin;

    @TableField("open_order_initial_margin")
    private BigDecimal openOrderInitialMargin;

    @TableField("max_withdraw_amount")
    private BigDecimal maxWithdrawAmount;

    @TableField("cross_wallet_balance")
    private BigDecimal crossWalletBalance;

    @TableField("cross_unPnl")
    private BigDecimal crossUnpnl;

    @TableField("available_balance")
    private BigDecimal availableBalance;

    @TableField("margin_available")
    private Boolean marginAvailable;

    @TableField("updated_time")
    private Long updatedTime;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;


}
