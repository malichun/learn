package com.hspedu.reflection.classload_;

/**
 * @author: malichun
 * @date 2022/4/23 0023 21:35
 */
public class ClassLoad02 {

}
class A{
    // 属性-成员变量-字段
    // 分析类加载的链接阶段-准备 属性是如何处理的
    // 1. n1 是实例属性, 不是静态变量, 因此在准备阶段, 是不会
    public int n1 = 10;
    public static int n2 = 20;
    public static final int n3 = 30;
}
