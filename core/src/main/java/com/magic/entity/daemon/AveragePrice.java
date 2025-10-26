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
public class AveragePrice {
    String        id;
    int           exchangeId;
    int           type;
    String        symbol;
    String        averagePriceInterval;
    BigDecimal    averagePrice;
    long          tsLastUpdate;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
