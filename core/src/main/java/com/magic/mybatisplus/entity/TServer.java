package com.magic.mybatisplus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
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
 * @since 2023-08-13
 */
@Getter
@Setter
@TableName("t_server")
@ApiModel(value = "TServer对象", description = "")
public class TServer implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("cloud_name")
    private String cloudName;

    @TableField("server_name")
    private String serverName;

    @TableField("server_ip")
    private String serverIp;

    @TableField("server_port")
    private String serverPort;

    @TableField("api_key_id")
    private Long apiKeyId;

    @ApiModelProperty("运行的symbol")
    @TableField("symbol")
    private String symbol;

    @ApiModelProperty("健康检查状态")
    @TableField("status_online")
    private String statusOnline;

    @ApiModelProperty("服务器在线状态")
    @TableField("status_ping")
    private String statusPing;

    @ApiModelProperty("量化程序运行状态")
    @TableField("status_quant")
    private String statusQuant;

    @ApiModelProperty("电话，逗号分隔")
    @TableField("notification_phone")
    private String notificationPhone;

    @TableField("notification_email")
    private String notificationEmail;

    @TableField("notification_lark")
    private String notificationLark;

    @TableField("alarm_interval_sms")
    private Integer alarmIntervalSms;

    @TableField("alarm_interval_email")
    private Integer alarmIntervalEmail;

    @TableField("alarm_interval_phone")
    private Integer alarmIntervalPhone;

    @TableField("alarm_interval_lark")
    private Integer alarmIntervalLark;

    @TableField("created_time")
    private LocalDateTime createdTime;

    @TableField("updated_time")
    private LocalDateTime updatedTime;


}
