package com.atguigu.chapter11_direct_memory.java;

import java.nio.ByteBuffer;
import java.util.Scanner;

/**
 * @description:
 * @author: malichun
 * @time: 2021/4/11/0011 15:17
 *
 *
 */
public class BufferTest {
    private static final int BUFFER = 1024 * 1024 * 1024;//1GB

    public static void main(String[] args){
        //直接分配本地内存空间
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(BUFFER);
        System.out.println("直接内存分配完毕，请求指示！");

        Scanner scanner = new Scanner(System.in);
        scanner.next();

        System.out.println("直接内存开始释放！");
        byteBuffer = null;
        System.gc();
        scanner.next();
    }
}
