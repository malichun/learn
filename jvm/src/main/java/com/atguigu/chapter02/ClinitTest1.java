package com.atguigu.chapter02;

/**
 * @description:
 * @author: malichun
 * @time: 2021/3/3/0003 15:04
 */
public class ClinitTest1 {
    static class Father{
        public static int A = 1;

        static{
            A = 2;
        }
    }
    static class Son extends Father{
        public static int B = A;
    }

    public static void main(String[] args) {
        System.out.println(Son.B);
    }
}
