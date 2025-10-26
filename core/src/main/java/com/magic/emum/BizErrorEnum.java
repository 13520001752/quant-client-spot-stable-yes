package com.magic.emum;

/**
 * 业务异常码枚举类
 *
 * @author sevenmagicbeans
 * @date 2022/9/3
 */
public enum BizErrorEnum {
    SUCCESS(0, "ok"),

    /**
     * 代表未知错误
     */
    UNKNOWN(99900001, "service error"),

    //999 为示例错误码前缀，各自微服务错误码需要下面地址登记：https://zktx07alin.larksuite.com/wiki/wikusfTNbkvLv64mzscTFZBpmp2
    REQUEST_PARAM_ERROR(99900000, "参数有误，请重试"),

    ERROR_NOT_INIT_CLIENT(1000000, "client isn't init"),
    ERROR_NOT_INIT_SYMBOL(1000001, "symbol isn't init"),
    ERROR_NOT_INIT_PRICE_SCALE(1000002, "price scale isn't init"),

    ERROR_NOT_INIT_PERCENT_OPEN(1000003, "percent open t init"),
    ERROR_NOT_INIT_PERCENT_CLOSE(1000004, "percent close isn't init"),
    ERROR_NOT_INIT_AMOUNT_OPEN(1000005, "amount open isn't init"),
    ERROR_NOT_INIT_AMOUNT_CLOSE(1000006, "amount close isn't init"),

    ERROR_ORDER(1010000, "order operation error"),
    ERROR_ORDER_NOT_EXIST(1010001, "order isn't exist"),
    ERROR_ORDER_ID_INVALID(1010002, "order id isn't valid"),
    ERROR_ORDER_QUERY(1010003, "exception in query order"),
    ERROR_ORDER_GET_FAILED(1010004, "get open order failed"),
    ERROR_ORDER_LATEST_NOT_EXIST(1010005, "latest order isn't exist"),
    ERROR_ORDER_CANCEL_FAILED(1010006, "order cancel failed"),
    ERROR_ORDER_CANCEL_EXCEPTION (1010007, "order cancel failed"),
    ERROR_GET_ACCOUNT_INFO (1010008, "failed to get account info"),

    ERROR_ORDER_OPEN_GET (1010009, "failed to get order open"),
    ERROR_ORDER_HISTORY_GET(1010009, "failed to get order history"),

    ERROR_DATA_JSON(1020005, "invalid order data from source"),

    ERROR_ORDER_CANCEL_ALL_FAILED(1010008, "order cancel failed"),
    ERROR_ORDER_PLACE_FAILED(1010009, "failed to place order"),

    BAD_PARAM_PRICE(3000001, "price isn't valid"),
    BAD_PARAM_AMOUNT(3000002, "amount isn't valid"),

    EXCEPTION_JSON_PARSE (4000002, "exception in parse json"),

    ERROR_CONFIG_ID_INVALID(5000001, "config id isn't valid"),
    ERROR_CONFIG_NOT_EXIST (5000002, "config id isn't valid"),
    ERROR_SYMBOL_CONFIG_NOT_EXIST (5000003, "symbol config id isn't valid"),

    ERROR_POSITION_SIDE_GET(5000004, "failed to get position side"),
    ERROR_POSITION_SIDE_SET(5000005, "failed to set position side"),


    ERROR_LEVERAGE_SET(5000006, "failed to set leverage"),

    ERROR_STOP_FAILED_NOT_STARTED(5000007, "service isn't started"),

    ERROR_STARTED_ALREADY (5000008, "service was started already"),

    ERROR_PRICE_INVALID (5000009, "invalid price"),
    ERROR_NOT_STARTED (5000010, "service isn't started"),
    ERROR_PRICE_SCALE_INVALID (5000012, "price precision is invalid"),
    ERROR_QUANTITY_SCALE_INVALID (5000013, "quantity precision is invalid"),
    ERROR_CONFIG_ID_MISMATCH(5000014, "config id mismatch"),
    ERROR_REDIS_INVALID (5000015, "redis isn't valid"),

    FAILED_LEVERAGE_SET(6000002, "failed to set leverage"),
    FAILED_LISTEN_START (6000003, "failed to wait listen task"),
    FAILED_SYNC_OPEN_ORDER (6000004, "failed to sync open orders"),
    FAILED_SYNC_HISTORY_ORDER (6000005, "failed to sync history orders"),
    FAILED_THREAD_HELPER_START (6000006, "failed to start thread helper"),

    FAILED_LISTEN_KEY_RECREATE (6000007, "failed to re-create listen key"),



    RESPON_ERROR(99900001, "业务异常");

    /**
     * 异常码
     */
    Integer code;
    /**
     * 异常信息
     */
    String  msg;

    BizErrorEnum(Integer code, String msg) {
        this.code = code;
        this.msg  = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
