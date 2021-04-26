package com.atguigu.chapter09_metaspace.java1;


/**
 * Created by John.Ma on 2021/4/1 0001 23:32
 */
public class StaticObjTest {
    static class Test {
        //静态属性
        static ObjectHolder staticObj = new ObjectHolder();
        //非静态属性
        ObjectHolder instanceObj = new ObjectHolder();

        void foo() {
            //局部变量
            ObjectHolder localObj = new ObjectHolder();
            System.out.println("done");
        }
    }

    private static class ObjectHolder {
    }

    public static void main(String[] args) {
        Test test = new StaticObjTest.Test();
        test.foo();
    }

}
