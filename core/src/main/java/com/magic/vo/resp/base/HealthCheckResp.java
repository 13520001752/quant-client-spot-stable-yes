package com.magic.vo.resp.base;

import lombok.Data;

/**
 * @author Tom.Hardy
 * @date 2023/11/27 22:37
 */

@Data
public class HealthCheckResp {
    long sequenceId;
    long timestamp;
    long configId;
}
