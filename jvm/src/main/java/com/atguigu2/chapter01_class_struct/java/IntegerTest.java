package com.atguigu2.chapter01_class_struct.java;

/**
 * @description:
 * @author: malichun
 * @time: 2021/5/18/0018 16:49
 */
public class IntegerTest {
    public static void main(String[] args) {
        Integer x = 5;
        int y = 5;
        System.out.println(x == y);  //true

        Integer i1 = 10;
        Integer i2 = 10;
        System.out.println(i1 == i2); //true

        Integer i3 = 128;
        Integer i4 = 128;
        System.out.println(i3 == i4); //false

    }

}
