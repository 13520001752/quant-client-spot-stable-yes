package com.magic.vo.resp.base;

import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.magic.emum.BizErrorEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author sevenmagicbeans
 * @date 2022/8/28
 */
@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@ToString
public class ResponseBase implements Serializable {

    @ApiModelProperty(value = "响应码，0为成功", required = true)
    private Integer code = 1; //0为成功，需要业务接口自己设置

    @ApiModelProperty(value = "响应说明")
    private String message;

    @ApiModelProperty(value = "响应的秒级时间戳")
    private Long time = DateUtil.currentSeconds();

    private Object data;

    public ResponseBase() {
    }

    public ResponseBase(BizErrorEnum code) {
        this.code = code.getCode();
        message   = code.getMsg();
    }

    public ResponseBase(BizErrorEnum code, Object data) {
        this.code    = code.getCode();
        this.message = code.getMsg();
        this.data    = data;
    }

    public static ResponseBase GetResponseSuccess() {
        ResponseBase resp = new ResponseBase();

        resp.setCode(0);
        resp.setMessage("success");
        return resp;
    }

    public static ResponseBase success () {
        ResponseBase resp = new ResponseBase();

        resp.setCode(0);
        resp.setMessage("success");
        return resp;
    }

    public static ResponseBase GetResponseSuccess(Object data) {
        ResponseBase resp = new ResponseBase();

        resp.setCode(0);
        resp.setMessage("success");
        resp.setData(data);
        return resp;
    }

    public static ResponseBase success (Object data) {
        ResponseBase resp = new ResponseBase();

        resp.setCode(0);
        resp.setMessage("success");
        resp.setData(data);
        return resp;
    }

    public static ResponseBase fail (BizErrorEnum code, Object data) {
        ResponseBase resp = new ResponseBase();

        resp.setCode(code.getCode());
        resp.setMessage(code.getMsg());
        resp.setData(data);
        return resp;
    }

    public static ResponseBase fail (int code, String message, Object data) {
        ResponseBase resp = new ResponseBase();

        resp.setCode(code);
        resp.setMessage(message);
        resp.setData(data);
        return resp;
    }

    public static ResponseBase fail (BizErrorEnum code) {
        ResponseBase resp = new ResponseBase();

        resp.setCode(code.getCode());
        resp.setMessage(code.getMsg());
        return resp;
    }

    public boolean isSuccess () {
        return code.intValue() == 0;
    }

    public boolean isFailed () {
        return code.intValue() != 0;
    }
}
