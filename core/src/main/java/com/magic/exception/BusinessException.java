package com.magic.exception;

import com.magic.emum.BizErrorEnum;

public class BusinessException extends RuntimeException {

    private int code;


    public BusinessException(String message) {
        super(message);
        this.code = BizErrorEnum.UNKNOWN.getCode();
    }

    public BusinessException(BizErrorEnum businessExceptionEnum){
        super(businessExceptionEnum.getMsg());
        this.code = businessExceptionEnum.getCode();
    }

    public BusinessException(BizErrorEnum businessExceptionEnum, String message){
        super(message);
        this.code = businessExceptionEnum.getCode();
    }


    public BusinessException(int code,String message){
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
