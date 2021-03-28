package com.atguigu.chapter05_stack;

import java.util.Date;

/**
 * @description:
 * @author: malichun
 * @time: 2021/3/11/0011 17:46
 */
public class LocalVariablesTest {
    private int count = 0;
    public static void main(String[] args) {
        LocalVariablesTest test = new LocalVariablesTest();
        int num = 10;
        test.test1();
    }

    public void test1(){
        Date date = new Date();
        String name1 = "baidu.com";
        String info = test2(date,name1);
        System.out.println(date + name1);
    }

    public String test2(Date dateP, String name2){
        dateP = null;
        name2 = "xiexu";
        double weight = 185.5; //占2个slot
        char gender = '男';
        return dateP + name2;
    }

    public void test4(){
        int a = 0;
        {
            int b = 0;
            b = a + 1;
        }
        //变量c使用 之前已经销毁的变量b占据的slot的位置
        int c = a + 1;
    }

    /**
     变量的分类: 按照数据类型分:①基本数据类型 ②引用数据类型
            按照在类中的位置分: ①成员变量: 在使用前,都经历过默认初始化赋值
                                    类变量: linking的prepare给类变量默认赋值 --> initial类变量显示赋值,即静态代码快赋值
                                    实例变量: 随着对象的创建,会在堆空间中分配实例变量空间,并进行默认赋值
                              ②局部变量: 在使用前必须要显示赋值的,否则编译不通过
     */
    public void test5Temp(){
        int num;
        // System.out.println(num);  //错误信息:Variable 'num' might not have been initialized
    }

}
