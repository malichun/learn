package com.atguigu2.chapter04_class_loader.java;

/**
 * Created by John.Ma on 2021/6/6 0006 10:03
 */
public class UserTest {
    public static void main(String[] args) {
        User user = new User(); //隐式加载
        try {
            //显式加载
            Class.forName("com.atguigu2.chapter04_class_loader.java.User");
            ClassLoader.getSystemClassLoader().loadClass("com.atguigu2.chapter04_class_loader.java.User");
        } catch (Exception e){

        }
    }
}
