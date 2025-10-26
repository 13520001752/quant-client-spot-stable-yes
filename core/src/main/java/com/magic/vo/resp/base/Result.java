package com.magic.vo.resp.base;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.magic.emum.BizErrorEnum;
import com.magic.exception.BusinessException;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "响应对象")
public class Result<T extends Serializable> {

	public static final String CODE_STR = "code";
	public static final String MSG_STR = "message";
	public static final String DATA_STR = "data";
	public static final String TOTAL_COUNT = "total_count";
	public static final String PAGE_NUM = "page_num";
	public static final String PAGE_SIZE = "page_size";

	@ApiModelProperty(value = "响应码")
	@JsonProperty("code")
	@Builder.Default
	private Integer code = BizErrorEnum.SUCCESS.getCode();

	@ApiModelProperty(value = "响应消息描述")
	@JsonProperty("message")
	@Builder.Default
	private String message = BizErrorEnum.SUCCESS.getMsg();

//	@ApiModelProperty(value = "扩展响应码")
//	@JsonProperty("ext_code")
//	private String extCode;

	@ApiModelProperty(value = "响应对象")
	private T data;

	@ApiModelProperty(value = "当前时间")
	@JsonProperty("time")
	@Builder.Default
	private String time = defaultTimeNow();

	public Result(int code, String msg) {
		this.code = code;
		this.message = msg;
		this.time = defaultTimeNow();
	}

	static ObjectMapper oMapper = new ObjectMapper();

	static String defaultTimeNow() {
		return String.valueOf(System.currentTimeMillis());
	}


	public static <T extends Serializable> Result<T> success(T data) {

		Result<T> result = new Result<T>();
		result.setData(data);
		result.setCode(0);
		return result;
	}


	/**
	 * 返回失败响应
	 */
	public static <T extends Serializable> Result<T> fail(BusinessException ex) {
		return new Result<T>(ex.getCode(), ex.getMessage());
	}

	/**
	 * 返回失败响应
	 */
	public static <T extends Serializable> Result<T> fail(BizErrorEnum businessExceptionEnum) {
		return new Result<T>(businessExceptionEnum.getCode(), businessExceptionEnum.getMsg());
	}

	/**
	 * 返回失败响应
	 */
	public static <T extends Serializable> Result<T> fail(BizErrorEnum businessExceptionEnum, String msg) {
		return new Result<T>(businessExceptionEnum.getCode(), msg);
	}

	/**
	 * 返回失败响应
	 */
	public static <T extends Serializable> Result<T> fail(int code, String msg) {
		return new Result<T>(code, msg);
	}

}
