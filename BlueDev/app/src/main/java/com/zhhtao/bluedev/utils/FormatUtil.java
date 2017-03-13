package com.zhhtao.bluedev.utils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zhangHaiTao on 2016/5/6.
 */
public class FormatUtil {

    /**
     * 将long类型的ms数 转换成日期
     *
     * @param time
     * @return
     */
    public static String getDate(long time) {
        String date;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        date = format.format(time);
        return date;
    }

    /**
     * 获取当前日期
     *
     * @return
     */
    public static String getNowDate() {
        return getDate(new Date().getTime());
    }

    /**
     * 将long类型的ms数 转换成日期和时间
     *
     * @param time
     * @return
     */
    public static String getDateTime(long time) {
        String date;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        date = format.format(time);
        return date;
    }

    /**
     * 获取当前日期和时间
     *
     * @return
     */
    public static String getNowDateTime() {
        return getDateTime(new Date().getTime());
    }

    /**
     * 根据字符串生成日期
     *
     * @param dateString
     * @return
     */
    Date getDateWithDateString(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;

        try {
            date = dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }


    /**
     * 格式化文件大小单位
     * * @param size
     * * @return
     */
    public static String getFormatSize(double size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            return size + "Byte";
        }
        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB";
        }
        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB";
        }
        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB";
    }

}
