package com.atguigu3.chapter03_gui;

import java.util.ArrayList;
import java.util.Random;

/**
 *  -Xms60m -Xms60m -XX:SurvivorRatio=8
 */
public class OOMTest {
    public static void main(String[] args) {
        ArrayList<Picture> list = new ArrayList<>();
        while(true){
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            list.add(new Picture(new Random().nextInt(100 * 500)));
        }

    }



}

class Picture{
    private byte[] pixels;

    public Picture(int length){
        this.pixels = new byte[length];
    }
}