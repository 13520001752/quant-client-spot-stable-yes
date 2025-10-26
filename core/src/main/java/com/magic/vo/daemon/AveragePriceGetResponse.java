package com.magic.vo.daemon;

import com.magic.entity.daemon.AveragePrice;
import com.magic.entity.daemon.MarkPrice;
import com.magic.service.BinanceOrderHttp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AveragePriceGetResponse implements java.io.Serializable {
    int          code;
    String       message;
    AveragePrice averagePrice;
    MarkPrice    markPrice;
}
