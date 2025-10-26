package com.magic.mybatisplus.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author magic beans
 * @since 2023-11-28
 */
@Getter
@Setter
@Builder
@TableName("t_symbol_config")
@ApiModel(value = "TSymbolConfig对象", description = "")
public class TSymbolConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("site:symbol 全部小写")
    @TableId("id")
    private String id;

    @TableField("site")
    private String site;

    @TableField("symbol")
    private String symbol;

    @TableField("tickSize")
    private Integer tickSize;

    @ApiModelProperty("价格保留几位小数，范围0-6")
    @TableField("priceLatest")
    private BigDecimal priceLatest;

    @ApiModelProperty("数量保留几位小数，")
    @TableField("quantityPrecise")
    private Integer quantityPrecise;

    @TableField("createdAt")
    private LocalDateTime createdAt;

    @TableField("updatedAt")
    private LocalDateTime updatedAt;


}
