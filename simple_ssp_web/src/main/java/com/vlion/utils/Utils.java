package com.vlion.utils;

/**
 * @description:
 * @author: malichun
 * @time: 2021/3/23/0023 15:18
 */
public class Utils {
    public static String getObjectValueString(Object o){
        return o == null ? "":o.toString();
    }

    public static double getObjectValueDouble(Object o){
        return o == null ? 0.0: Double.parseDouble(o.toString());
    }

    public static int getObjectValueInteger(Object o){
        return o == null ? 0:(int)Double.parseDouble(o.toString());
    }

    public static String convertDateStr(String origin){
        return origin.substring(0,4)+"-"+origin.substring(4,6)+"-"+origin.substring(6,8);
    }

}
