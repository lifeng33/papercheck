package com.fengcloud.papercheck.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CommonUtil {

    /**
     * 获取ID : 年月日时分秒毫秒 + 6位随机数
     *
     * @return ID
     */
    public static String getId() {
        // 获取年月日时分秒毫秒字符串
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        // 格式化日期字符串
        String dateStr = sdf.format(new Date());
        // 生成随机数
        Double randomDouble = Math.random();
        // 返回日期字符串 + 随机数前六位
        return dateStr + randomDouble.toString().substring(2, 8);
    }

    /**
     * 获取指定长度的随机数字
     *
     * @param length
     * @return
     */
    public static String getRandomNumber(int length) {
        // 生成随机数
        Double randomDouble = Math.random();
        int start = 2;
        int end = start + length;
        String num = randomDouble.toString().substring(start, end);
        return num;
    }

}
