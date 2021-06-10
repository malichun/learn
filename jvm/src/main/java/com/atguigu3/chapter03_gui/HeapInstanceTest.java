package com.atguigu3.chapter03_gui;

import java.util.Random;

/**
 *  -Xms600m -Xmx600m -XX:SurvivorRatio=8
 */
public class HeapInstanceTest {
    byte[] buffer =  new byte[new Random().nextInt(1024*100)];

    public static void main(String[] args) {

    }

}
