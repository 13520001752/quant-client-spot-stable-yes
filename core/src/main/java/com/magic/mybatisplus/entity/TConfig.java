package com.magic.mybatisplus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author magic beans
 * @since 2023-12-01
 */
@Data
@TableName("t_config")
@ApiModel(value = "TConfig对象", description = "")
public class TConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("唯一ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("账户名称")
    @TableField("accountName")
    private String accountName;

    @ApiModelProperty("账户别名")
    @TableField("accountAlias")
    private String accountAlias;

    @ApiModelProperty("APIKEY")
    @TableField("apiKey")
    private String apiKey;

    @ApiModelProperty("apiSecret")
    @TableField("apiSecret")
    private String apiSecret;

    @TableField("apiPhrase")
    private String apiPhrase;

    @ApiModelProperty("服务器名称，quant1, quant2")
    @TableField("serverName")
    private String serverName;

    @ApiModelProperty("服务器IP")
    @TableField("serverIP")
    private String serverIP;

    @ApiModelProperty("服务器Port")
    @TableField("serverPort")
    private Integer serverPort;

    @ApiModelProperty("BTCUSDT,ETHUSDT,...")
    @TableField("symbol")
    private String symbol;

    @TableField("leverage")
    private Integer leverage;

    @TableField("positionMax")
    private BigDecimal positionMax;

    @TableField("positionMin")
    private BigDecimal positionMin;

    @TableField("positionInit")
    private BigDecimal positionInit;

    @TableField("amountOpen")
    private BigDecimal amountOpen;

    @TableField("amountClose")
    private BigDecimal amountClose;

    @TableField("percentOpen")
    private BigDecimal percentOpen;

    @TableField("percentClose")
    private BigDecimal percentClose;

    @TableField("percentStop")
    private BigDecimal percentStop;

    @ApiModelProperty("管理员名字")
    @TableField("adminName")
    private String adminName;

    @ApiModelProperty("管理员电话")
    @TableField("adminPhone")
    private String adminPhone;

    @ApiModelProperty("管理员Email")
    @TableField("adminEmail")
    private String adminEmail;

    @ApiModelProperty("lark群名1")
    @TableField("larkName1")
    private String larkName1;

    @ApiModelProperty("lark群名2")
    @TableField("larkName2")
    private String larkName2;

    @TableField("tsCreated")
    private LocalDateTime tsCreated;

    @TableField("tsUpdated")
    private LocalDateTime tsUpdated;

    @ApiModelProperty("管理后台更新，量化程序服务器状态：online, offline，调用healthCheck接口")
    @TableField("statusService")
    private String statusService;

    @ApiModelProperty("量化程序更新，量化程序程序状态：'stopped', 'running'")
    @TableField("statusQuant")
    private String statusQuant;

    @ApiModelProperty("量化程序更新，量化程序最后存活时间")
    @TableField("pingTime")
    private Long pingTime;

    @ApiModelProperty("管理后台更新，市场最新价")
    @TableField("priceLast")
    private BigDecimal priceLast;

    @ApiModelProperty("最后成交价格，由量化程序，管理后台不通阶段更新，")
    @TableField("priceLastMatch")
    private BigDecimal priceLastMatch;

    @ApiModelProperty("0:未删除，1:已删除")
    @TableField("deleted")
    @TableLogic
    private Integer deleted;

    @ApiModelProperty("tbd")
    @TableField("configStatus")
    private Integer configStatus;


}
