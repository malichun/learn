package com.atguigu2.chapter04_class_loader.java;

/**
 * @description:
 * @author: malichun
 * @time: 2021/6/6/0006 16:09
 */
public class ClassLoaderTest1 {
    public static void main(String[] args) {
        //获取系统类加载器
        ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader(); //
        System.out.println(systemClassLoader); // sun.misc.Launcher$AppClassLoader@18b4aac2

        //获取扩展类加载器
        ClassLoader extClassLoader = systemClassLoader.getParent();
        System.out.println(extClassLoader); // sun.misc.Launcher$ExtClassLoader@61bbe9ba

        //试图获取引导类加载器失败了
        ClassLoader bootstrapClassLoader = extClassLoader.getParent();
        System.out.println(bootstrapClassLoader); // null ,引导类加载器是c/c++编写的


        //########################核心api
        try {
            //java.lang.String类的加载器是根类加载器
            ClassLoader classLoader = Class.forName("java.lang.String").getClassLoader();
            System.out.println(classLoader); // null

            //当前自定义类的类加载器,默认使用系统类加载器
            ClassLoader classLoader2 = Class.forName("com.atguigu2.chapter04_class_loader.java.ClassLoaderTest1").getClassLoader();
            System.out.println(classLoader2); // sun.misc.Launcher$AppClassLoader@18b4aac2


            //关于数组类型的加载  !!!!!!!!!!!!!!!!!!! 数组
            String[] arrStr = new String[10];
            System.out.println(arrStr.getClass());  //class [Ljava.lang.String;

            // 跟数组中元素的类型加载器是一样的,java.lang.String使用的引导类加载器,自定义类的类加载器是系统类加载器
            System.out.println(arrStr.getClass().getClassLoader()); // null  java.lang.String的加载器是引导类加载器

            ClassLoaderTest1[] arr1 = new ClassLoaderTest1[10];
            System.out.println(arr1.getClass().getClassLoader());  // sun.misc.Launcher$AppClassLoader@18b4aac2


            int[] arr2 = new int[10];
            System.out.println(arr2.getClass().getClassLoader()); // null !!! 基本数据类型不需要加载的,不是跟类加载器

            //线程的classLoader是系统类加载器
            System.out.println(Thread.currentThread().getContextClassLoader()); //sun.misc.Launcher$AppClassLoader@18b4aac2


        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
