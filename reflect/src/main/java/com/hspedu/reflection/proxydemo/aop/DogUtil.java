package com.hspedu.reflection.proxydemo.aop;

/**
 * @author: malichun
 * @date 2022/4/23 0023 16:20
 */
public class DogUtil {
    // 第一个拦截器方法
    public void method1(){
        System.out.println("====模拟第一个通用方法====");
    }

    // 第二个拦截方法
    public void method2(){
        System.out.println("====模拟第二个通用方法====");
    }
}
