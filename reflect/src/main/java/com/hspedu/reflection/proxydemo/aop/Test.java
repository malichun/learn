package com.hspedu.reflection.proxydemo.aop;

/**
 * @author: malichun
 * @date 2022/4/23 0023 16:34
 */
public class Test {
    public static void main(String[] args) throws Exception{
        // 创建一个原始的GunDog对象, 作为target
        Dog target = new GunDog();
        // 以指定target来创建动态代理对象
        Dog dog = (Dog)MyProxyFactory.getProxy(target);
        dog.run();
        dog.info();
    }
}
