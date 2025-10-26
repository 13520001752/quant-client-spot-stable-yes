package com.magic.vo.req.base;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author sevenmagicbeans
 * @date 2022/8/28
 * 分页请求基类，统一管理分页
 */
@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ReqPageBase implements Serializable {

    @ApiModelProperty(value="第几页,不填默认1",example = "1",notes = "1")
    @ApiParam(defaultValue = "1")
    private Integer pageNum = 1;

    @ApiModelProperty(value="一页多少条,不填默认12",example = "12",notes = "12")
    private Integer pageSize = 12;

}
