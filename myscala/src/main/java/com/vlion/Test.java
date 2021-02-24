package com.vlion;

/**
 * @description:
 * @author: malichun
 * @time: 2020/11/9/0009 15:36
 */
public class Test {
    public static void main(String[] args) {
        System.out.println(ChhUtil.strAddComma("10000000a0"));
    }

    public static void testFenGeNumber(){
        String number = "100000000";
        StringBuffer sb = new StringBuffer(number);
        for(int i =number.length()-3;i>=0;i-=3 ){
            sb.insert(i, ",");
        }
        System.out.println(sb);
    }
}
