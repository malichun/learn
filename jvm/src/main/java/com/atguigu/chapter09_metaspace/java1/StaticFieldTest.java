package com.atguigu.chapter09_metaspace.java1;

/**
 * Created by John.Ma on 2021/4/1 0001 23:26
 */
public class StaticFieldTest {

    private static byte[] arr = new byte[1024 * 1024 * 100];

    public static void main(String[] args) {
        System.out.println(StaticFieldTest.arr);
    }
}
