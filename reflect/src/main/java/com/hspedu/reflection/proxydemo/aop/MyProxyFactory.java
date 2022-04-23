package com.hspedu.reflection.proxydemo.aop;

import java.lang.reflect.Proxy;

/**
 * @author: malichun
 * @date 2022/4/23 0023 16:26
 */
public class MyProxyFactory {
    // 为指定的target生成动态代理对象
    public static Object getProxy(Object target) throws Exception{
        // 创建爱你一个MyInvocationHandler对象
        MyInvocationHandler handler = new MyInvocationHandler();
        // 为MyInvocationHandler设置target对象
        handler.setTarget(target);
        // 创建并返回一个动态代理
        return Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(), handler);
    }
}
