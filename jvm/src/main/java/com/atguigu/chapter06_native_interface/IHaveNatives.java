package com.atguigu.chapter06_native_interface;

/**
 * @description:
 * @author: malichun
 * @time: 2021/3/15/0015 19:09
 */
public class IHaveNatives {
    public native void Native1(int x);

    public native static long Native2();

    private native synchronized float Native3(Object o);

    native void Native4(int[] ary) throws Exception;

}
