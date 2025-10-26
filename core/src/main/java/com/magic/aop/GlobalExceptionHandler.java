package com.magic.aop;



import com.magic.emum.BizErrorEnum;
import com.magic.exception.BusinessException;
import com.magic.vo.resp.base.ResponseBase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

/**
 * 全局异常拦截器
 * 针对restful的请求的统一拦截，返回错误码给到前端
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 分割符
     */
    private static final String SEP_COMMA = ",";

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseBase handlerException(HttpServletRequest request, RuntimeException e) {

        ResponseBase response = new ResponseBase();
        response.setMessage("system error");
        response.setCode(BizErrorEnum.REQUEST_PARAM_ERROR.getCode());
        log.error("error: ",e);
        return response;

    }


    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public ResponseBase handlerHttpException(HttpServletRequest request, RuntimeException e) {

        ResponseBase response = new ResponseBase();
        response.setMessage("system error");
        response.setCode(BizErrorEnum.REQUEST_PARAM_ERROR.getCode());
        log.error("error: ",e);
        return response;

    }

    /**
     * 业务异常,直接返回错误码
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseBody
    public ResponseBase businessException(HttpServletRequest request, BusinessException e) {
        ResponseBase response = new ResponseBase();
        response.setMessage(e.getMessage());
        response.setCode(e.getCode());
        return response;

    }

    /**
     * bean参数验证 带requestbody的方式.
     *
     * @param e RuntimeException
     * @return String
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseBase validExceptionHandler(MethodArgumentNotValidException e) {
        ResponseBase response = new ResponseBase();
        String errorMsg =  e.getBindingResult().getAllErrors().stream().map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining(","));
        response.setMessage(errorMsg);
        response.setCode(BizErrorEnum.REQUEST_PARAM_ERROR.getCode());
        return response;
    }

    /**
     * bean参数验证.
     *
     * @param e RuntimeException
     * @return String
     */
    @ExceptionHandler(BindException.class)
    @ResponseBody
    public ResponseBase validExceptionHandler(BindException e) {
        ResponseBase response = new ResponseBase();
        String errorMsg = e.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining(SEP_COMMA));
        response.setMessage(errorMsg);
        response.setCode(BizErrorEnum.REQUEST_PARAM_ERROR.getCode());

        return response;

    }

    /**
     * 单个参数验证.
     *
     * @param e RuntimeException
     * @return String
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    public ResponseBase validExceptionHandler(ConstraintViolationException e) {
        ResponseBase response = new ResponseBase();
        String errorMsg = HttpStatus.BAD_REQUEST.value()+""+e.getConstraintViolations().stream().map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(SEP_COMMA));
        response.setMessage(errorMsg);
        response.setCode(BizErrorEnum.REQUEST_PARAM_ERROR.getCode());
        return response;
    }

}
