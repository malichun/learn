package com.atguigu.chapter13_stringtable.java1;


/**
 * @description:
 * @author: malichun
 * @time: 2021/4/19/0019 18:33
 */
public class StringTest5 {
    public static void test1() {
        String s1 = "a" + "b" + "c";  // 得到 abc的常量池
        String s2 = "abc"; // abc存放在常量池，直接将常量池的地址返回
        /**
         * 最终java编译成.class，再执行.class
         */
        System.out.println(s1 == s2); // true，因为存放在字符串常量池
        System.out.println(s1.equals(s2)); // true
    }

    public static void test2() {
        String s1 = "javaEE";
        String s2 = "hadoop";
        String s3 = "javaEEhadoop";
        String s4 = "javaEE" + "hadoop";
        String s5 = s1 + "hadoop";
        String s6 = "javaEE" + s2;
        String s7 = s1 + s2;

        System.out.println(s3 == s4); // true
        System.out.println(s3 == s5); // false
        System.out.println(s3 == s6); // false
        System.out.println(s3 == s7); // false
        System.out.println(s5 == s6); // false
        System.out.println(s5 == s7); // false
        System.out.println(s6 == s7); // false

        String s8 = s6.intern();
        System.out.println(s3 == s8); // true
    }

    public static void test3(){
        String s1 = "a";
        String s2 = "b";
        String s3 = "ab";
        /*
        * 如下的s1 + s2
        *StringBuilder s = new StringBuilder();
            s.append(s1);
            s.append(s2);
            s.toString(); -> 类似于new String("ab");
        * */
        String s4 = s1 + s2;

        System.out.println(s3 == s4);
    }

    public static void test4(){
        final String s1 = "a";
        final String s2 = "b";
        String s3 = "ab";
        String s4 = s1 + s2;
        System.out.println(s3 == s4);
    }


    public static void main(String[] args) {
        test1();
        test2();
        test3();

    }
}
