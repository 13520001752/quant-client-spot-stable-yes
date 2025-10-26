package com.magic.mybatisplus.entity;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;

import com.magic.constant.Constants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * <p>
 * 
 * </p>
 *
 * @author magic beans
 * @since 2023-11-23
 */
@Slf4j
@Getter
@Setter
@TableName("t_order")
@ApiModel(value = "TOrder对象", description = "")
public class TOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("订单ID")
    @TableId("order_id")
    private String orderId;

    @ApiModelProperty("订单ID自定义")
    @TableField("order_id_link")
    private String orderIdLink;

    @ApiModelProperty("交易所ID")
    @TableField("exchange_id")
    private Integer exchangeId;

    @ApiModelProperty("apiKey")
    @TableField("api_key")
    private String apiKey;

    @ApiModelProperty("交易对")
    @TableField("symbol")
    private String symbol;

    @ApiModelProperty("BUY 买入, SELL 卖出")
    @TableField("order_side")
    private String orderSide;

    @ApiModelProperty("订单类型:MARKET 市价单, LIMIT 限价单, STOP 止损单, TAKE_PROFIT 止盈单, LIQUIDATION 强平单")
    @TableField("order_type")
    private String orderType;

    @ApiModelProperty("订单类型-原始")
    @TableField("order_type_original")
    private String orderTypeOriginal;

    @ApiModelProperty("原始价格")
    @TableField("order_price_original")
    private BigDecimal orderPriceOriginal;

    @ApiModelProperty("平均价格")
    @TableField("order_price_average")
    private BigDecimal orderPriceAverage;

    @ApiModelProperty("原始数量")
    @TableField("order_quantity_original")
    private BigDecimal orderQuantityOriginal;

    @ApiModelProperty("累计已成交量")
    @TableField("order_quantity_execute")
    private BigDecimal orderQuantityExecute;

    @ApiModelProperty("累计成交价值")
    @TableField("cumulative_quote")
    private BigDecimal cumulativeQuote;

    @ApiModelProperty("是否是只减仓单")
    @TableField("close_position")
    private Boolean closePosition;

    @ApiModelProperty("触发价格:对TRAILING_STOP_MARKET【无效】")
    @TableField("price_stop")
    private BigDecimal priceStop;

    @ApiModelProperty("是否仅减仓: true, false")
    @TableField("reduce_only")
    private Boolean reduceOnly;

    @ApiModelProperty("有效方法: GTC, IOC, FOK, GTX")
    @TableField("time_in_force")
    private String timeInForce;

    @ApiModelProperty("持仓方向, 单向:可填BOTH, 双向LONG 或 SHORT")
    @TableField("position_side")
    private String positionSide;

    @ApiModelProperty("订单状态: NEW, PARTIALLY_FILLED, FILLED, CANCELED, EXPIRED, EXPIRED_IN_MATCH")
    @TableField("order_status")
    private String orderStatus;

    @ApiModelProperty("实现盈亏")
    @TableField("pnl")
    private BigDecimal pnl;

    @ApiModelProperty("手续费币种")
    @TableField("fee_coin")
    private String feeCoin;

    @ApiModelProperty("手续费数量")
    @TableField("fee_amount")
    private BigDecimal feeAmount;

    @ApiModelProperty("订单创建时间,1579276756075")
    @TableField("order_time_create")
    private Long orderTimeCreate;

    @ApiModelProperty("订单更新时间")
    @TableField("order_time_update")
    private Long orderTimeUpdate;

    @ApiModelProperty("跟踪ID")
    @TableField("track_id")
    private Integer trackId;

    @ApiModelProperty("跟踪timestamp")
    @TableField("track_timestamp")
    private Long trackTimestamp;

    @ApiModelProperty("是否删除, 1:deleted, 0:normal")
    @TableField("deleted")
    @TableLogic
    private Integer deleted;

    @ApiModelProperty("创建时间")
    @TableField("created_at")
    private LocalDateTime createdAt;

    @ApiModelProperty("最后更新时间")
    @TableField("updated_at")
    private LocalDateTime updatedAt;

    @ApiModelProperty("任务ID，用于隔离日志，订单")
    @TableField("task_id")
    private String taskId;

    public static JSONObject compare(TOrder order1, TOrder order2) {
        JSONObject jo = new JSONObject();

        if (!order1.getOrderId().equalsIgnoreCase(order2.getOrderId())) {
            log.info("OrderId:{} -> {}", order1.getOrderId(), order2.getOrderId());
        }

        if (!order1.getOrderIdLink().equalsIgnoreCase(order2.getOrderIdLink())) {
            log.info("OrderIdLink:{} -> {}", order1.getOrderIdLink(), order2.getOrderIdLink());
        }
        if (order1.getExchangeId().intValue() != order2.getExchangeId().intValue()) {
            log.info("exchangeId:{} -> {}", order1.getExchangeId(), order2.getExchangeId());
        }
        if (!order1.getApiKey().equalsIgnoreCase(order2.getApiKey())) {
            log.info("ApiKey:{} -> {}", order1.getApiKey(), order2.getApiKey());
        }
        if (!order1.getSymbol().equalsIgnoreCase(order2.getSymbol())) {
            log.info("Symbol:{} -> {}", order1.getSymbol(), order2.getSymbol());
        }
        if (!order1.getOrderSide().equalsIgnoreCase(order2.getOrderSide())) {
            log.info("OrderSide:{} -> {}", order1.getOrderSide(), order2.getOrderSide());
        }
        if (!order1.getOrderType().equalsIgnoreCase(order2.getOrderType())) {
            log.info("OrderType:{} -> {}", order1.getOrderType(), order2.getOrderType());
        }

        if (!order1.getOrderTypeOriginal().equalsIgnoreCase(order2.getOrderTypeOriginal())) {
            log.info("OrderTypeOriginal:{} -> {}", order1.getOrderTypeOriginal(), order2.getOrderTypeOriginal());
        }
        if (!order1.getOrderPriceOriginal().equals(order2.getOrderPriceOriginal())) {
            log.info("OrderPriceOriginal:{} -> {}", order1.getOrderPriceOriginal(), order2.getOrderPriceOriginal());
        }
        if (!order1.getOrderPriceAverage().equals(order2.getOrderPriceAverage())) {
            log.info("OrderPriceAverage:{} -> {}", order1.getOrderPriceAverage(), order2.getOrderPriceAverage());
        }

        if (!order1.getOrderQuantityOriginal().equals(order2.getOrderQuantityOriginal())) {
            log.info("OrderQuantityOriginal:{} -> {}", order1.getOrderQuantityOriginal(), order2.getOrderQuantityOriginal());
        }
        if (!order1.getOrderQuantityExecute().equals(order2.getOrderQuantityExecute())) {
            log.info("OrderQuantityExecute:{} -> {}", order1.getOrderQuantityExecute(), order2.getOrderQuantityExecute());
        }
        if (!order1.getCumulativeQuote().equals(order2.getCumulativeQuote())) {
            log.info("CumulativeQuote:{} -> {}", order1.getCumulativeQuote(), order2.getCumulativeQuote());
        }

        if (order1.getClosePosition() != order2.getClosePosition()) {
            log.info("ClosePosition:{} -> {}", order1.getClosePosition(), order2.getClosePosition());
        }
        if (order1.getPriceStop().equals(order2.getPriceStop())) {
            log.info("PriceStop:{} -> {}", order1.getPriceStop(), order2.getPriceStop());
        }
        if (order1.getReduceOnly() != order2.getReduceOnly()) {
            log.info("ReduceOnly:{} -> {}", order1.getReduceOnly(), order2.getReduceOnly());
        }
        if (!order1.getTimeInForce().equalsIgnoreCase(order2.getTimeInForce())) {
            log.info("TimeInForce:{} -> {}", order1.getTimeInForce(), order2.getTimeInForce());
        }
        if (!order1.getPositionSide().equalsIgnoreCase(order2.getPositionSide())) {
            log.info("PositionSide:{} -> {}", order1.getPositionSide(), order2.getPositionSide());
        }

        if (!order1.getOrderStatus().equalsIgnoreCase(order2.getOrderStatus())) {
            log.info("OrderStatus:{} -> {}", order1.getOrderStatus(), order2.getOrderStatus());
        }

        if (order1.getOrderTimeCreate() != order2.getOrderTimeCreate()) {
            log.info("OrderTimeCreate:{} -> {}", order1.getOrderTimeCreate(), order2.getOrderTimeCreate());
        }
        if (order1.getOrderTimeUpdate() != order2.getOrderTimeUpdate()) {
            log.info("OrderId:{} -> {}", order1.getOrderTimeUpdate(), order2.getOrderTimeUpdate());
        }
        if (order1.getTrackId() != order2.getTrackId()) {
            log.info("TrackId:{} -> {}", order1.getTrackId(), order2.getTrackId());
        }
        if (order1.getTrackTimestamp() != order2.getTrackTimestamp()) {
            log.info("TrackTimestamp:{} -> {}", order1.getTrackTimestamp(), order2.getTrackTimestamp());
        }
        return jo;
    }

    public JSONObject compare(TOrder order1) {
        JSONObject jo = new JSONObject();

        if (!order1.getOrderId().equalsIgnoreCase(this.getOrderId())) {
            log.info("OrderId:{} -> {}", order1.getOrderId(), this.getOrderId());
        }

        if (!order1.getOrderIdLink().equalsIgnoreCase(this.getOrderIdLink())) {
            log.info("OrderIdLink:{} -> {}", order1.getOrderIdLink(), this.getOrderIdLink());
        }
        if (order1.getExchangeId().intValue() != this.getExchangeId().intValue()) {
            log.info("exchangeId:{} -> {}", order1.getExchangeId(), this.getExchangeId());
        }
        if (!order1.getApiKey().equalsIgnoreCase(this.getApiKey())) {
            log.info("ApiKey:{} -> {}", order1.getApiKey(), this.getApiKey());
        }
        if (!order1.getSymbol().equalsIgnoreCase(this.getSymbol())) {
            log.info("Symbol:{} -> {}", order1.getSymbol(), this.getSymbol());
        }
        if (!order1.getOrderSide().equalsIgnoreCase(this.getOrderSide())) {
            log.info("OrderSide:{} -> {}", order1.getOrderSide(), this.getOrderSide());
        }
        if (!order1.getOrderType().equalsIgnoreCase(this.getOrderType())) {
            log.info("OrderType:{} -> {}", order1.getOrderType(), this.getOrderType());
        }

        if (order1.getOrderTypeOriginal().equalsIgnoreCase(this.getOrderTypeOriginal())) {
            log.info("OrderTypeOriginal:{} -> {}", order1.getOrderTypeOriginal(), this.getOrderTypeOriginal());
        }
        if (order1.getOrderPriceOriginal().compareTo(this.getOrderPriceOriginal()) != 0) {
            log.info("OrderPriceOriginal:{} -> {}", order1.getOrderPriceOriginal(), this.getOrderPriceOriginal());
        }
        if (order1.getOrderPriceAverage().compareTo(this.getOrderPriceAverage()) != 0) {
            log.info("OrderPriceAverage:{} -> {}", order1.getOrderPriceAverage(), this.getOrderPriceAverage());
        }

        if (order1.getOrderQuantityOriginal().compareTo(this.getOrderQuantityOriginal()) != 0) {
            log.info("OrderQuantityOriginal:{} -> {}", order1.getOrderQuantityOriginal(), this.getOrderQuantityOriginal());
        }
        if (order1.getOrderQuantityExecute().compareTo(this.getOrderQuantityExecute()) != 0) {
            log.info("OrderQuantityExecute:{} -> {}", order1.getOrderQuantityExecute(), this.getOrderQuantityExecute());
        }
        if (order1.getCumulativeQuote().compareTo(this.getCumulativeQuote()) != 0) {
            log.info("CumulativeQuote:{} -> {}", order1.getCumulativeQuote(), this.getCumulativeQuote());
        }

        if (order1.getClosePosition() != this.getClosePosition()) {
            log.info("ClosePosition:{} -> {}", order1.getClosePosition(), this.getClosePosition());
        }
        if (order1.getPriceStop().equals(this.getPriceStop())) {
            log.info("PriceStop:{} -> {}", order1.getPriceStop(), this.getPriceStop());
        }
        if (order1.getReduceOnly() != this.getReduceOnly()) {
            log.info("ReduceOnly:{} -> {}", order1.getReduceOnly(), this.getReduceOnly());
        }
        if (!order1.getTimeInForce().equalsIgnoreCase(this.getTimeInForce())) {
            log.info("TimeInForce:{} -> {}", order1.getTimeInForce(), this.getTimeInForce());
        }
        if (!order1.getPositionSide().equalsIgnoreCase(this.getPositionSide())) {
            log.info("PositionSide:{} -> {}", order1.getPositionSide(), this.getPositionSide());
        }

        if (!order1.getOrderStatus().equalsIgnoreCase(this.getOrderStatus())) {
            log.info("OrderStatus:{} -> {}", order1.getOrderStatus(), this.getOrderStatus());
        }

        if (order1.getOrderTimeCreate().longValue() != this.getOrderTimeCreate().longValue()) {
            log.info("OrderTimeCreate:{} -> {}", order1.getOrderTimeCreate(), this.getOrderTimeCreate());
        }
        if (order1.getOrderTimeUpdate().longValue() != this.getOrderTimeUpdate().longValue()) {
            log.info("OrderId:{} -> {}", order1.getOrderTimeUpdate(), this.getOrderTimeUpdate());
        }
        if (order1.getTrackId() != this.getTrackId()) {
            log.info("TrackId:{} -> {}", order1.getTrackId(), this.getTrackId());
        }
        if (order1.getTrackTimestamp().longValue() != this.getTrackTimestamp().longValue()) {
            log.info("TrackTimestamp:{} -> {}", order1.getTrackTimestamp(), this.getTrackTimestamp());
        }

        return jo;
    }

//    public static boolean parse(String orderIdLink) {
//        if (StringUtils.isBlank(orderIdLink)) {
//            return true;
//        }
//
//        String[] split = orderIdLink.split("-");
//        if (split.length != 4) {
//            return true;
//        }
//
//        switch (split[0]) {
//            case Constants.orderPrefix:
//            case Constants.orderPrefixInitBuy:
//                break;
//            default:
//                return true;
//        }
//        return false;
//    }


    // buy
    // sell
    // 订单号生成：常规订单
//    public static HashMap<String, String> generateOrderIdLink_Normal(int seqId) {
//        Long ts = System.currentTimeMillis();
//
//        String orderIdBuy  = String.format("%s-%d-%s-%d", Constants.orderPrefix, seqId, "b", ts);
//        String orderIdSell = String.format("%s-%d-%s-%d", Constants.orderPrefix, seqId, "s", ts);
//
//        HashMap<String, String> map = new HashMap<>();
//        map.put("orderIdBuy", orderIdBuy);
//        map.put("orderIdSell", orderIdSell);
//        return map;
//    }

    // 订单号生成：常规订单
//    public static HashMap<String, String> generateOrderIdLink_Normal(int seqId, long ts) {
//        String orderIdBuy  = String.format("%s-%d-%s-%d", Constants.orderPrefix, seqId, "b", ts);
//        String orderIdSell = String.format("%s-%d-%s-%d", Constants.orderPrefix, seqId, "s", ts);
//
//        HashMap<String, String> map = new HashMap<>();
//        map.put("orderIdBuy", orderIdBuy);
//        map.put("orderIdSell", orderIdSell);
//        return map;
//    }
//
//    // 订单号生成：初始订单
//    public static HashMap<String, String> generateOrderIdLink_Init(int seqId) {
//        Long ts = System.currentTimeMillis();
//
//        String orderIdBuy  = String.format("%s-%d-%s-%d", Constants.orderPrefixInitBuy, seqId, "b", ts);
//        String orderIdSell = String.format("%s-%d-%s-%d", Constants.orderPrefixInitBuy, seqId, "s", ts);
//
//        HashMap<String, String> map = new HashMap<>();
//        map.put("orderIdBuy", orderIdBuy);
//        map.put("orderIdSell", orderIdSell);
//        return map;
//    }

    // 订单号生成：初始订单
//    public static String generateOrderIdLink_Stop(int seqId) {
//        Long ts = System.currentTimeMillis();
//
//        String orderIdBuy = String.format("%s-%d-%s-%d", Constants.orderPrefix, seqId, "stop", ts);
//
//        return orderIdBuy;
//    }

    public static JSONObject getBriefInfo(TOrder order) {
        JSONObject jo = new JSONObject();

        jo.put("orderId", order.getOrderId());
        jo.put("getOrderIdLink", order.getOrderIdLink());
        jo.put("getOrderTypeOriginal", order.getOrderTypeOriginal());
        jo.put("getOrderType", order.getOrderType());
        jo.put("getOrderPriceOriginal", order.getOrderPriceOriginal());
        jo.put("getOrderPriceAverage", order.getOrderPriceAverage());
        jo.put("getPriceStop", order.getPriceStop());
        jo.put("getOrderStatus", order.getOrderStatus());
        return jo;
    }

}
