package com.atguigu.chapter05_stack;

/**
 * @description:
 * @author: malichun
 * @time: 2021/3/15/0015 14:30
 */
public class DynamicLinkingTest {

    int num = 10 ;

    public void methodA(){
        System.out.println("methodA()....");
    }

    public void methodB(){
        System.out.println("methodB()....");
        methodA();
        num ++;
    }

}
