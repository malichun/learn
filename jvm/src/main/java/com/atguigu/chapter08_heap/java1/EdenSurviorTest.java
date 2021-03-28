package com.atguigu.chapter08_heap.java1;

/**
 * -Xms600m -Xmx600m
 *
 *
 * -XX:SurvivorRatio=8: 设置新生代中Eden区域Survivor区的比例
 * -XX:NewRatio :设置新生代与老年代的比例,默认值是 2
 *
 * -XX:-UseAdaptiveSizePolicy  : 自适应机制,关闭自适应的内存分配策略 -U... ( -不用,+使用 )
 * -Xmn: 设置新生代的空间的大小
 *
 */
public class EdenSurviorTest {
    public static void main(String[] args) {
        System.out.println("我是来打酱油的~");
        try {
            Thread.sleep(1000000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
