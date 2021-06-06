package com.atguigu2.chapter02_instruction.java;

import java.util.Date;

/**
 * Created by John.Ma on 2021/5/29 0029 15:17
 */
public class LoadAndStoreTest {
    // 局部变量压栈指令
    public void load(int num, Object obj, long count, boolean flag, short[] arr) {
        System.out.println(num);
        System.out.println(obj);
        System.out.println(count);
        System.out.println(flag);
        System.out.println(arr);
    }

    // 常量入栈指令
    public void pushConstLdc() {
        int a = 5;
        int b = 6;
        int c = 127;
        int d = 128;
        int e = 1234567;
    }

    public void constLdc() {
        long a1 = 1;
        long a2 = 2;
        float b1 = 2;
        float b2 = 3;
        double c1 = 1;
        double c2 = 2;
        Date d = null;
    }

    // 出栈装入局部变量表指令
    public void store(int k, double d) {
        int m = k + 2;
        int l = 12;
        String str = "atguigu";
        float f = 10.0F;
        d = 10;
    }

    public void foo(long l, float f) {
        {
            int i = 0;
        }
        {
            String s = "hello world";
        }
    }
}
