package com.hspedu.reflection.classload_;

/**
 * 演示类加载-初始化阶段
 * @author: malichun
 * @date 2022/4/23 0023 22:51
 */
public class ClassLoad03 {
    public static void main(String[] args) {
        // 分析
        // 1. 加载B类, 并生成 B的class对象
        // 2. 链接 num = 0
        // 3. 初始化阶段
        // 依次自动收集类中的所有静态变量的赋值动作和静态代码块中的语句
        /*
        clinit(){
            System.out.println("B 静态代码块被执行");
            num = 300;
            num = 100;
        }
        合并num = 100;
        */

        System.out.println(B.num); // 100, 如果直接使用类的静态变量属性, 也会导致类的加载

        // 如果new对象会执行构造器
        // new B(); // 类加载
        // 4. 执行构造器

    }
}

class B{
    static {
        System.out.println("B 静态代码块被执行");
        num = 300;
    }
    static int num = 100;

    public B(){ // 构造器
        System.out.println("构造器被执行");
    }
}
