package com.atguigu.chapter02.classloader;

/**
 * @description:
 * @author: malichun
 * @time: 2021/3/3/0003 16:16
 */
public class ClassLoaderTest2 {
    public static void main(String[] args) throws ClassNotFoundException {

        Class<?> aClass = Class.forName("java.lang.String");
        ClassLoader classLoader = aClass.getClassLoader();
        System.out.println(classLoader);

        //2.
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        System.out.println(contextClassLoader);

        //3.
        ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
        System.out.println(systemClassLoader);
        System.out.println(systemClassLoader.getParent());

    }
}
