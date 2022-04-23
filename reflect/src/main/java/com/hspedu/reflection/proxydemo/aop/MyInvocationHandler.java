package com.hspedu.reflection.proxydemo.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 *
 * @author: malichun
 * @date 2022/4/23 0023 16:21
 */
public class MyInvocationHandler implements InvocationHandler {
    // 需要被代理的对象
    private Object target;

    public void setTarget(Object target) {
        this.target = target;
    }

    // 执行动态代理对象的所有方法时, 都会被替换成执行如下的invoke方法
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        DogUtil dogUtil = new DogUtil();
        // 指定DogUtil对象中的method1方法
        dogUtil.method1();
        // 以target作为主调来执行method方法
        Object result = method.invoke(target, args);
        // 执行DogUtil 对象中的method2 方法
        dogUtil.method2();
        return result;
    }
}
