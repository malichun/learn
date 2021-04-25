package com.atguigu.chapter13_stringtable.java1;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * @description:
 * @author: malichun
 * @time: 2021/4/18/0018 16:27
 */
public class StringTest2 {
    public static void main(String[] args) {

        BufferedReader br = null;
        try{
            br = new BufferedReader(new FileReader("words.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }
}
