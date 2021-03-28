package com.atguigu.chapter05_stack;

/**
 * @description:
 * @author: malichun
 * @time: 2021/3/15/0015 16:45
 */
class Father {
    public Father() {
        System.out.println("father的构造器");
    }

    public static void showStatic(String str) {
        System.out.println("father " + str);
    }

    public final void showFinal() {
        System.out.println("father show final");
    }

    public void showCommon() {
        System.out.println("father 普通方法");
    }
}

public class Son extends Father {
    public Son() {
        //invokespecial 非虚方法
        super();
    }

    public Son(int age) {
        //invokespecial 非虚方法
        this();
    }

    //不是重写的父类的静态方法，因为静态方法不能被重写！
    public static void showStatic(String str) {
        System.out.println("son " + str);
    }

    private void showPrivate(String str) {
        System.out.println("son private" + str);
    }

    public void show() {
        //invokestatic 非虚方法
        showStatic("baidu.com");

        //invokestatic 非虚方法
        super.showStatic("good!");

        //invokespecial 非虚方法
        showPrivate("hello!");

        //invokevirtual
        //虽然字节码指令中显示为invokevirtual，但因为此方法声明有final，不能被子类重写，所以也认为此方法是非虚方法。
        showFinal();

        //invokespecial 非虚方法
        super.showCommon();

        //invokevirtual 虚方法
        //有可能子类会重写父类的showCommon()方法
        showCommon();

        //invokevirtual 虚方法
        //info()是普通方法，有可能被重写，所以是虚方法
        info();

        MethodInterface in = null;
        //invokeinterface 虚方法
        in.methodA();
    }

    public void info() {

    }

    public void display(Father f) {
        f.showCommon();
    }

    public static void main(String[] args) {
        Son so = new Son();
        so.show();
    }
}

interface MethodInterface {
    void methodA();
}