package cn.itcast.netty.c2_bytebuffer;

import java.nio.ByteBuffer;

import static cn.itcast.netty.c2_bytebuffer.ByteBufferUtil.debugAll;

/**
 * 演示读
 * Created by John.Ma on 2021/10/3 0003 16:18
 */
public class TestByteBufferRead {
    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put(new byte[]{'a','b','c','d'});
        buffer.flip();

        // 从头开始读
//        buffer.get(new byte[4]);
//        debugAll(buffer);
//        buffer.rewind(); // 从头开始读
//        System.out.println((char) buffer.get());
//        debugAll(buffer);



        // mark & reset
        // mark 做一个标记,记录position位置,reset 将position重置到mark的位置
//        System.out.println((char)buffer.get()); // a
//        System.out.println((char)buffer.get()); // b
//        buffer.mark(); // 加标记,索引为2的位置
//        System.out.println((char)buffer.get()); // c
//        System.out.println((char)buffer.get()); // d
//        buffer.reset(); // 将position重置到索引2
//        System.out.println((char)buffer.get()); // c
//        System.out.println((char)buffer.get()); // d



        // get(i),不会改变读指针
        System.out.println((char)buffer.get(3));
        debugAll(buffer);

    }
}
