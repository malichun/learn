package com.atguigu.chapter12_execution_engine;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @author: malichun
 * @time: 2021/4/18/0018 14:52
 */
public class JITTest {
    public static void main(String[] args) {
        List<String> list = new ArrayList<String>();

        for( int i = 0;i<1000;i++){
            list.add("让天下没有难学的技术");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }

}
