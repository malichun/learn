package com.atguigu2.chapter03_load_linking_init.java1;

import org.junit.Test;

import java.util.Random;

/**
 * Created by John.Ma on 2021/6/5 0005 18:41
 */
public class Test2 {
    @Test
    public void test3(){
        try {
            ClassLoader.getSystemClassLoader().loadClass("com.atguigu2.chapter03_load_linking_init.java1.Person");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
}

class Person{
    static{
        System.out.println("Person类的初始化");
    }

    public static final int NUM = 1; // 在链接过程的准备阶段就被赋值为1了
    public static final int NUM1 = new Random().nextInt(10);
}
