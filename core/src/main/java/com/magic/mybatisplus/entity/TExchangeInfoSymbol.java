package com.magic.mybatisplus.entity;

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
 * 
 * </p>
 *
 * @author magic beans
 * @since 2023-08-29
 */
@Getter
@Setter
@TableName("t_exchange_info_symbol")
@ApiModel(value = "TExchangeInfoSymbol对象", description = "")
public class TExchangeInfoSymbol implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("id")
    private String id;

    @ApiModelProperty("交易所名称")
    @TableField("exchange_id")
    private Integer exchangeId;

    @ApiModelProperty("USDT")
    @TableField("symbol")
    private String symbol;

    @ApiModelProperty("是否可用作保证金")
    @TableField("pair")
    private String pair;

    @ApiModelProperty("保证金资产自动兑换阈值")
    @TableField("contract_type")
    private String contractType;

    @TableField("date_delivery")
    private LocalDateTime dateDelivery;

    @TableField("date_onboard")
    private LocalDateTime dateOnboard;

    @ApiModelProperty("状态")
    @TableField("status")
    private String status;

    @ApiModelProperty("请忽略")
    @TableField("margin_percent_maint")
    private BigDecimal marginPercentMaint;

    @ApiModelProperty("请忽略")
    @TableField("margin_percent_required")
    private BigDecimal marginPercentRequired;

    @ApiModelProperty("标的资产")
    @TableField("asset_base")
    private String assetBase;

    @ApiModelProperty("报价资产")
    @TableField("asset_quote")
    private String assetQuote;

    @ApiModelProperty("保证金资产")
    @TableField("asset_margin")
    private String assetMargin;

    @ApiModelProperty("价格小数点位数(仅作为系统精度使用，注意同tickSize 区分）")
    @TableField("precision_price")
    private Integer precisionPrice;

    @ApiModelProperty("数量小数点位数(仅作为系统精度使用，注意同stepSize 区分）")
    @TableField("precision_quantity")
    private Integer precisionQuantity;

    @ApiModelProperty("标的资产精度")
    @TableField("precision_base_asset")
    private Integer precisionBaseAsset;

    @ApiModelProperty("报价资产精度")
    @TableField("precision_quote")
    private Integer precisionQuote;

    @TableField("underlying_type")
    private String underlyingType;

    @TableField("underlying_sub_type")
    private String underlyingSubType;

    @TableField("settle_plan")
    private Integer settlePlan;

    @TableField("trigger_protect")
    private BigDecimal triggerProtect;

    @ApiModelProperty("价格限制, 价格上限, 最大价格")
    @TableField("filter_price_max")
    private BigDecimal filterPriceMax;

    @ApiModelProperty("价格限制, 价格下限, 最小价格")
    @TableField("filter_price_min")
    private BigDecimal filterPriceMin;

    @ApiModelProperty("价格限制, 订单最小价格间隔")
    @TableField("filter_price_tick_size")
    private BigDecimal filterPriceTickSize;

    @ApiModelProperty("数量限制, 数量上限, 最大数量")
    @TableField("filter_lot_qty_max")
    private BigDecimal filterLotQtyMax;

    @ApiModelProperty("数量限制, 数量下限, 最小数量")
    @TableField("filter_lot_qty_min")
    private BigDecimal filterLotQtyMin;

    @ApiModelProperty("数量限制, 订单最小数量间隔")
    @TableField("filter_lot_step_size")
    private BigDecimal filterLotStepSize;

    @ApiModelProperty("市价订单数量限制, 数量上限, 最大数量")
    @TableField("filter_market_lot_qty_max")
    private BigDecimal filterMarketLotQtyMax;

    @ApiModelProperty("市价订单数量限制, 数量下限, 最小数量")
    @TableField("filter_market_lot_qty_min")
    private BigDecimal filterMarketLotQtyMin;

    @ApiModelProperty("市价订单数量限制, 允许的步进值")
    @TableField("filter_market_lot_step_size")
    private BigDecimal filterMarketLotStepSize;

    @ApiModelProperty("最多订单数限制")
    @TableField("filter_order_num_max")
    private Integer filterOrderNumMax;

    @ApiModelProperty("最多条件订单数限制")
    @TableField("filter_stop_order_num_max")
    private Integer filterStopOrderNumMax;

    @ApiModelProperty("最小名义价值")
    @TableField("filter_notional_min")
    private BigDecimal filterNotionalMin;

    @ApiModelProperty("价格上限百分比")
    @TableField("filter_multiplier_up")
    private BigDecimal filterMultiplierUp;

    @ApiModelProperty("价格下限百分比")
    @TableField("filter_multiplier_down")
    private BigDecimal filterMultiplierDown;

    @TableField("filter_multiplier_decimal")
    private BigDecimal filterMultiplierDecimal;

    @ApiModelProperty("强平费率")
    @TableField("liquidation_fee")
    private BigDecimal liquidationFee;

    @ApiModelProperty("市价吃单(相对于标记价格)允许可造成的最大价格偏离比例")
    @TableField("market_take_bound")
    private BigDecimal marketTakeBound;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;


}
