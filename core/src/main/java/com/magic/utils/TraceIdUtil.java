package com.magic.utils;

import com.magic.constant.Constants;
import cn.hutool.core.util.RandomUtil;
import io.micrometer.core.instrument.util.StringUtils;
import org.slf4j.MDC;


/**
 * 增加TraceId操作的工具类，提供traceId的默认取值、setter、getter和生成
 *
 * @author sevenmagicbeans
 * @date 2022/7/5
 */
public class TraceIdUtil {
    /**
     * 当traceId为空时，显示的traceId。随意。
     */
    private static final String DEFAULT_TRACE_ID = "0";

    /**
     * 设置traceId
     */
    public static void setTraceId(String traceId) {
        //如果参数为空，则设置默认traceId
        traceId = StringUtils.isBlank(traceId) ? DEFAULT_TRACE_ID : traceId;
        //将traceId放到MDC中
        MDC.put(Constants.TRACE_ID, traceId);
    }

    /**
     * 获取traceId
     */
    public static String getTraceId() {
        //获取
        String traceId = MDC.get(Constants.TRACE_ID);
        //如果traceId为空，则返回默认值
        return StringUtils.isBlank(traceId) ? getRandomTraceId() : traceId;
    }

    /**
     * 判断traceId为默认值
     */
    public static Boolean defaultTraceId(String traceId) {
        return DEFAULT_TRACE_ID.equals(traceId);
    }

    /**
     * 生成traceId
     */
    public static String getRandomTraceId() {
        return RandomUtil.randomString(32);
    }

}
