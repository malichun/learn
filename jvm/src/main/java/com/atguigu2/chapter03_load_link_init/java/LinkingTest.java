package com.atguigu2.chapter03_load_link_init.java;

/**
 * 过程二：链接阶段（以下都是针对static修饰的变量）
 * 基本数据类型：非 final 修饰的变量，在准备环节进行默认初始化赋值。
 *             final修饰以后，在准备环节直接进行显示赋值。
 *
 * 拓展：如果使用字面量的方式定义一个字符串的常量的话，也是在准备环节直接进行显示赋值。
 */
public class LinkingTest {
    private static long id;  // 默认初始化赋值
    private static final int num = 1;  // 显示赋值

    public static final String constStr = "CONST";  // 显示赋值


    public static final String constStr1 = new String("CONST");

    public static void main(String[] args) {
        System.out.println(constStr1);
    }
}
