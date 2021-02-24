package com.vlion.interceptor;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;

/**
 * @description:
 * @author: malichun
 * @time: 2020/12/7/0007 16:33
 */
public class JSONUtils {

    public static boolean isJSONValidate(String log){
        try{
            JSON.parse(log);
            return true;
        }catch (JSONException e){
            return false;
        }
    }
}
