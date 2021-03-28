package com.atguigu.chapter05_stack.java2;

/**
 * @description:
 * @author: malichun
 * @time: 2021/3/15/0015 17:28
 */
public class VirtualMethodTable {

}

interface Friendly {
    void sayHello();
    void sayGoodbye();
}

class Dog {
    public void sayHello() {
    }
    @Override
    public String toString() {
        return "Dog";
    }
}

class Cat implements Friendly {
    public void eat() {
    }
    public void sayHello() {
    }
    public void sayGoodbye() {
    }
    protected void finalize() {
    }
    public String toString() {
        return "Cat";
    }
}

class CockerSpaniel extends Dog implements Friendly {
    public void sayHello() {
        super.sayHello();
    }
    public void sayGoodbye() {
    }
}