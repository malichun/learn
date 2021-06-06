package com.atguigu2.chapter04_class_loader.java1;

import java.lang.reflect.Method;

/**
 * Created by John.Ma on 2021/6/6 0006 23:29
 */
public class LoopRun {
    public static void main(String[] args) {
        while(true){
            try{
                // 1.创建自定义类加载器的实例
                MyClassLoader loader = new MyClassLoader("D:\\fileImportant\\Learn_projects\\learn\\jvm\\src\\main\\java");
                //2.加载指定的类
                Class clazz = loader.findClass("com.atguigu2.chapter04_class_loader.java1.Demo1");
                //3.创建运行时类的实例
                Object demo = clazz.newInstance();
                //4.获取运行时类中指定的方法
                Method m = clazz.getMethod("hot");
                //5.调用指定的方法
                m.invoke(demo);
                Thread.sleep(5000);

            }catch (Exception e){
                System.out.println("not found");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

}
