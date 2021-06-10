package com.atguigu3.chapter02_console;

import java.util.ArrayList;

/**
 * @description:
 * @author: malichun
 * @time: 2021/6/10/0010 10:00
 */
public class GCTest {
    public static void main(String[] args) {
        ArrayList<byte[]> list = new ArrayList<>();

        for (int i = 0; i < 1000; i++) {
            byte[] arr = new byte[1024 * 100];  // 100KB
            list.add(arr);
            try {
                Thread.sleep(60);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
