package com.atguigu.chapter13_stringtable.java2;

/**
 * @description:
 * @author: malichun
 * @time: 2021/4/20/0020 13:30
 */
public class StringIntern {
    public static void main(String[] args) {
        String s = new String("1"); // 堆空间的new String(),已经将"1"放入到常量池了
        s.intern();  //将该对象放入到常量池。但是调用此方法没有太多的区别，因为已经存在了1,应该是s的引用没有改变,还是在堆中的引用
        String s2 = "1"; // 常量池中的地址
        System.out.println(s == s2);  //jdk6中:false    jdk8中:false

        String s3 = new String("1") + new String("1");
        s3.intern(); //s的引用还是堆中的引用,常量池中的"11"是在堆中的引用
        String s4 = "11"; //
        System.out.println(s3 == s4);  //jdk6中:false   jdk8中:true

    }

}
