package com.atguigu2.chapter02_instruction.java;
import org.junit.Test;

/**
 * Created by John.Ma on 2021/5/29 0029 19:56
 */
public class ArithmeticTest  {
    @Test
    public void test1() {
        double d = 0.0;
        System.out.println(1 / d);//Infinity
    }

    @Test
    public void test2() {
        double d1 = 0.0;
        double d2 = 0.0;
        System.out.println(d1 / d2);//not a number
    }
}
