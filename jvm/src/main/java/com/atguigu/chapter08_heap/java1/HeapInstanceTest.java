package com.atguigu.chapter08_heap.java1;

import java.util.ArrayList;
import java.util.Random;

/**
 * @description:
 * @author: malichun
 * @time: 2021/3/27/0027 16:32
 *
 * -Xms600m -Xmx600m
 *
 */
public class HeapInstanceTest {
    byte[] buffer = new byte[new Random().nextInt(1024 * 200)];

    public static void main(String[] args) {
        ArrayList<HeapInstanceTest> list = new ArrayList<HeapInstanceTest>();
        while (true) {
            list.add(new HeapInstanceTest());
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
