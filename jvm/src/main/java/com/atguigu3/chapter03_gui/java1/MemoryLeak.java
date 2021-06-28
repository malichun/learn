package com.atguigu3.chapter03_gui.java1;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * 存在内存泄露
 */
public class MemoryLeak {
    public static void main(String[] args) {
        while(true){
            ArrayList beanList = new ArrayList();
            for(int i = 0;i<500;i++){
                Bean data = new Bean();
                data.list.add(new byte[1024*10]); // 10kb
                beanList.add(data);
            }
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class Bean{
    int size = 10;
    String info = "hello,atguigu";
    static ArrayList list = new ArrayList(); // 会存在内存泄漏
}
