package com.hspedu.reflection;

import com.hspedu.Cat;

import java.lang.reflect.Array;
import java.lang.reflect.Method;

/**
 * 测试反射调用性能, 和方案优化
 * @author: malichun
 * @date 2022/4/23 0023 14:08
 */
public class Reflection02 {
    public static void main(String[] args) throws Exception{
        m1();

        m2();

        m3();


    }

    // 普通方法hi
    public static void m1(){
        Cat cat = new Cat();
        long start = System.currentTimeMillis();
        for (int i = 0; i < 90000000; i++) {
            cat.hi();
        }
        long end = System.currentTimeMillis();
        System.out.println("m1() hi 耗时:"+(end-start));

    }

    // 反射机制调用方法
    public static void m2() throws Exception{
        Class<Cat> cls = Cat.class;
        Cat o = cls.newInstance();
        Method hi = cls.getMethod("hi");
        long start = System.currentTimeMillis();
        for (int i = 0; i < 90000000; i++) {
            hi.invoke(o);
        }
        long end = System.currentTimeMillis();
        System.out.println("m2() hi 耗时:"+(end-start));

    }

    // 反射优化 + 关闭访问检查
    public static void m3() throws Exception{
        Class<Cat> cls = Cat.class;
        Cat o = cls.newInstance();
        Method hi = cls.getMethod("hi");
        hi.setAccessible(true); // 在反射调用方法时, 取消访问检查
        long start = System.currentTimeMillis();
        for (int i = 0; i < 90000000; i++) {
            hi.invoke(o);
        }
        long end = System.currentTimeMillis();
        System.out.println("m2() hi 耗时:"+(end-start));

    }
}
