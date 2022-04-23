package com.hspedu.reflection.proxydemo;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author: malichun
 * @date 2022/4/23 0023 16:01
 */
public class ProxyTest {
    public static void main(String[] args) {
        // 创建一个InvocationHandler对象
        InvocationHandler handler = new MyInvocationHandler();
        // 使用指定的InvocationHandler来生成一个动态代理对象
        Person p = (Person) Proxy.newProxyInstance(Person.class.getClassLoader(),
            new Class[]{Person.class},
            handler
            );

        // 调用动态代理对象的walk() 和sayHello方法
        p.walk();
        p.sayHello("孙悟空");
    }
}

interface Person{
    void walk();
    void sayHello(String name);
}

class MyInvocationHandler implements InvocationHandler{
    /**
     * 执行动态代理对象的所有方法时, 都会被替换成执行如下的invoke方法
     *
     * @param proxy 动态代理对象
     * @param method 代表正在执行的方法
     * @param args 代表调用目标方法时传入的实参
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("----正在执行的方法:"+method);
        if(args!=null){
            System.out.println("下面是执行该方法的实参为: ");
            for(Object val : args){
                System.out.println(val);
            }
        }else{
            System.out.println("调用该方法没有实参! ");
        }
        return null;
    }
}


