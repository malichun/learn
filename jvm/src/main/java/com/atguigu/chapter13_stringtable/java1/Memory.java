package com.atguigu.chapter13_stringtable.java1;

/**
 * @description:
 * @author: malichun
 * @time: 2021/4/19/0019 13:21
 */
public class Memory {
    public static void main(String[] args) {//line 1
        int i = 1;//line 2
        Object obj = new Object();//line 3
        Memory mem = new Memory();//line 4
        mem.foo(obj);//line 5
    }//line 9

    private void foo(Object param) {//line 6
        String str = param.toString();//line 7
        System.out.println(str);
    }//line 8
}
