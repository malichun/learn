package cn.itcast.test;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * @description:
 * @author: malichun
 * @time: 2021/7/20/0020 18:34
 */
public class TestUnsafe {
    public static void main(String[] args) throws NoSuchFieldException {
        Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");



    }
}
