package com.atguigu.chapter02;

/**
 * @description:
 * @author: malichun
 * @time: 2021/3/3/0003 14:37
 */
public class ClassInitTest {
    static{
        num = 10;
    }

    static int num = 1;

    public static void main(String[] args) {
        System.out.println(ClassInitTest.num);
    }
}
