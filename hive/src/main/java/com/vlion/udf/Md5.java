package com.vlion.udf;

import org.apache.hadoop.hive.ql.exec.UDF;

import java.security.MessageDigest;

/**
 * @description:
 * @author: malichun
 * @time: 2021/3/29/0029 18:44
 */
public class Md5 extends UDF {
    public String evaluate (final String value) {
        if(value == null){
            return null;
        }
        StringBuilder sb = new StringBuilder();
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("md5");
            byte[] bytes = messageDigest.digest(value.getBytes());
            for (int i = 0; i < bytes.length; i++) {
                int tempInt = bytes[i] & 0xff;
                if (tempInt < 16) {
                    sb.append(0);
                }
                sb.append(Integer.toHexString(tempInt));
            }
        } catch (Exception e) {

        }
        return sb.toString();

    }

    public static void main(String[] args) {
        String hello = "hello world";
        System.out.println("MD5加密后的结果：" + new Md5().evaluate(hello));
    }




}
