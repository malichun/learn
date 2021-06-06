package com.atguigu2.chapter02_instruction.java;
import org.junit.Test;

/**
 * Created by John.Ma on 2021/5/29 0029 19:56
 */
public class ArithmeticTest  {
    @Test
    public void method1() {
        int i = 10;
        double j = i / 0.0;
        System.out.println(j); //无穷大  Infinity


        double d1 = 0.0;
        double d2 = d1 / 0.0;
        System.out.println(d2);//NaN
    }

    @Test
    public void test2() {
        double d1 = 0.0;
        double d2 = 0.0;
        System.out.println(d1 / d2);//not a number
    }


    public void m3(){
        int i =10;
        int a = i++;
        int j =20;
        int b = ++j;
        System.out.println(a); //10
        System.out.println(b); //21
    }


    public static void main(String[] args) {
        new ArithmeticTest().m3();
    }
}
