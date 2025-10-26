package com.magic.entity.daemon;

import cn.hutool.core.date.DateTime;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author Tom.Hardy
 * @date 30/8/23 01:13
 */

@Data
public class MarkPrice {
    String        id;
    int           exchangeId;
    int           type;
    String        symbol;
    BigDecimal    priceMark;
    BigDecimal    priceSpot;
    long          tsLastUpdate;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
