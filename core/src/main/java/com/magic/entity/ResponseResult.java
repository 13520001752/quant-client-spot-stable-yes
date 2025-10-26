package com.magic.entity;

import lombok.Data;

/**
 * @author sevenmagicbeans
 * @date 2022/12/21
 */
@Data
public class ResponseResult {
    private CompetitorParam inputParam; // 入参
    private CompetitorParamBatch inputParamBatch; // 批量入参
    private Boolean success; // 是否操作成功
    private Integer code; // 状态码
    private String message; // 错误信息
    private String data; // 数据
    private String url; // 需要请求的url
    private String requestParam; // 请求参数，json字符串格式
    private String requestMethod; // 请求方式，GET, POST...
}
