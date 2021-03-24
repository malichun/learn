package com.atguigu.chapter08_heap.java1;

/**
 * -Xms600m -Xmx600m
 */
public class EdenSurviorTest {
    public static void main(String[] args) {
        System.out.println("我是来打酱油的~");
        try {
            Thread.sleep(1000000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
