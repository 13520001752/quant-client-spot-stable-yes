package com.magic.vo.req.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.magic.service.BinanceOrderHttp;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderGetResponse implements java.io.Serializable{
    List<BinanceOrderHttp> orderList;
}
