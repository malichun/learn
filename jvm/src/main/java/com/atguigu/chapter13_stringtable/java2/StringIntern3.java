package com.atguigu.chapter13_stringtable.java2;

/**
 * @description:
 * @author: malichun
 * @time: 2021/4/21/0021 17:50
 */
public class StringIntern3 {
    public static void main(String[] args) {
       String s3 = new String("1") + new String("1");// 堆中的对象,目前字符串常量池中没有"11"
       s3.intern(); // 常量池"11"为上面堆的引用
       String s4 = "11";
        System.out.println(s3 == s4); // 都指向了堆中的地址

    }
}
