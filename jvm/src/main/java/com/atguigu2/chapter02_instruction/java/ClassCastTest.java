package com.atguigu2.chapter02_instruction.java;

import org.junit.Test;

/**
 * Created by John.Ma on 2021/5/29 0029 21:18
 */
public class ClassCastTest {
    @Test
    public void upCast() {
        int i = 10;
        long l = i;
        float f = i;
        double d = i;
        float f1 = l;
        double d1 = l;
        double d2 = f1;
    }


    @Test
    public void upCast2() {
        int i = 123123123;
        float f = i;
        System.out.println(f);//精度会丢失变成123123120
        long l = 123123123123L;
        double d = l;
        // 精度不会丢失因为还处于double的显示范围内
        System.out.println(d);//123123123123
        long l2 = 123123123123123123L;
        double d2 = l2;
        // 超出double的范围了，精度会丢失。
        System.out.println(d2);// 123123123123123120
    }

    @Test
    public void upCast3() {
//        int i = b;
//        long l = b;
//        float f = b;
    }

    @Test
    public void downCast1() {
        int i = 10;
        byte b = (byte) i;
        short s = (short) i;
        char c = (char) i;
        long l = 10L;
        int i1 = (int) l;
        byte b1 = (byte) l;
    }


    @Test
    public void downCast2(){
        short s = 10;
        byte b = (byte) s;
    }

    /**
     *
     */
    @Test
    public void downCast3(){
        int i = 128;
        byte b = (byte) i;
        System.out.println(b);// -128
    }

    @Test
    public void downCast4(){
        double d1 = Float.NaN;
        int i = (int) d1;
        // NaN
        System.out.println(d1);
        // 浮点值为NaN，转换结果为int或者long类型则按照0处理。
        System.out.println(i);

        // 正无穷大
        double d2 = Double.POSITIVE_INFINITY;
        long l = (long) d2;
        int j = (int) d2;
        // 9223372036854775807 既 long 的最大值
        System.out.println(l);
        // 2147483647 既 int的最大值
        System.out.println(j);

        float f1 = (float) d2;
        // Infinity
        System.out.println(f1);
    }
}
