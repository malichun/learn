package com.atguigu.chapter02;

/**
 * @description:
 * @author: malichun
 * @time: 2021/3/3/0003 14:50
 */
public class ClinitTest {
    private static int A = 10;
    private int b = 1;

    static {
        A = 20;
    }

    public ClinitTest() {
        b = 2;
    }

    public static void methodStatic(){int c = 1;}
    public void methodNonStatic(){int d = 2;}

}
