package com.atguigu.chapter08_heap.java2;

/**
 *  同步省略说明
 *
 */
public class SynchronizedTest {
    public void f(){
        Object hollis = new Object();
        synchronized (hollis){
            System.out.println("hello");
        }

    }
}
