package com.magic.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * 日期工具类
 *
 * @author sevenmagicbeans
 * @date 2022/7/31
 */
public class DateUtils {


    /**
     * 对分钟、小时、天 等大于等于0，小于10的 整数加0，返回字符串，如传入 0 则返回  "00"
     * @param timeInt
     * @return
     */
    public static String get24ToStr(int timeInt) {

        if (timeInt < 0) {
            throw new IllegalArgumentException("timeInt must be a positive number!");
        } else {
            StringBuffer sb = new StringBuffer();
            if (timeInt < 10) {
                sb.append("0").append(timeInt);
            }else{
                sb.append(timeInt);
            }
            return sb.toString();
        }
    }

    /**
     * 注意周一为一周第一天
     * 获取上一周第一天的时间
     * @return
     */
    public static String getWeekStartDay(int day,String formatType){
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_WEEK,2);
        c.add(Calendar.DATE,(-7+day));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatType);
        return simpleDateFormat.format(c.getTime());
    }

}
