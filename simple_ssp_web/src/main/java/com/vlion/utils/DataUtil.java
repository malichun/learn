package com.vlion.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

/**
 * @description:
 * @author: malichun
 * @time: 2021/3/23/0023 11:03
 */
public class DataUtil {
    private static final long ONE_DAY=24*60*60*1000;

    public static final String DATE_FORMAT1="yyyy-MM-dd";
    public static final String DATE_FORMAT2="yyyyMMdd";
    public static final String DATETIME_FORMAT1="yyyy-MM-dd HH:mm:ss";
    public static final String DATETIME_FORMAT2="yyyyMMddHHmmss";
    public static final String DATETIME_FORMAT3="yyyy-MM-dd HH:mm:ss.SSS";
    public static final String DATETIME_FORMAT4="yyyyMMddHHmmssSSS";

    public static final String TIME_FORMAT1="HH:mm:ss";


    //获取当前时间(字符串格式)
    public static String getCurrentDateTime(String format){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return LocalDateTime.now().format(formatter);
    }

    //LocalDateTime转String
    public static String getLocalDateTimeToStr(LocalDateTime localDateTime, String format){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(format);
        return localDateTime.format(dtf);
    }

    //将String转成LocalDateTime对象
    public static LocalDateTime parseStringToDateTime(String time, String format) {
     return null;
    }


}
