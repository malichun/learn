package com.atguigu2.chapter04_class_loader.java;

import java.net.URL;

/**
 * Created by John.Ma on 2021/6/6 0006 9:58
 */
public class ClassLoaderTest {
    public static void main(String[] args) throws ClassNotFoundException {
        System.out.println("***************启动类加载器********************");
        //获取BootstrapClassLoader能够加载的api路径
        URL[] urls = sun.misc.Launcher.getBootstrapClassPath().getURLs();
        for(URL element:urls){
            System.out.println(element.toExternalForm());
        }

        //从上面的路径中随意选择一个类,来看看他的类加载器是什么:引导类加载器(null)
        ClassLoader classLoader = java.security.Provider.class.getClassLoader();
        System.out.println(classLoader); // null

        System.out.println("***************扩展类加载器********************");
        String extDirs = System.getProperty("java.ext.dirs");
        for(String path:extDirs.split(";")) {
            System.out.println(path);
        }

        //从上面的路径中随意选择一个类,来看看他的类加载器是什么:扩展类加载器
        ClassLoader classLoader1 = sun.security.ec.CurveDB.class.getClassLoader();
        System.out.println(classLoader1); // sum.misc.Launcher$ExtClassLoader@1540e19d

        System.out.println(ClassLoader.getSystemClassLoader().loadClass("com.atguigu2.chapter04_class_loader.java.User"));
    }
}
