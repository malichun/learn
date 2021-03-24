package com.atguigu.chapter05_stack;

/**
 * @description:
 * @author: malichun
 * @time: 2021/3/11/0011 16:25
 *
 *  默认count:11420
 *  设置栈的大小   -Xss256 : 2474
 *
 */
public class StackError {
    private static int count=1;
    public static void main(String[] args) {
        System.out.println(count);
        count++;
        main(args);
    }


}
