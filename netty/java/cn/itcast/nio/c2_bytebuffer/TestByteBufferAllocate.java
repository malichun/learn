package cn.itcast.nio.c2_bytebuffer;

import java.nio.ByteBuffer;

/**
 * Created by John.Ma on 2021/10/3 0003 16:05
 */
public class TestByteBufferAllocate {
    public static void main(String[] args) {
        System.out.println(ByteBuffer.allocate(16).getClass());
        System.out.println(ByteBuffer.allocateDirect(16).getClass());
        /*
        class java.nio.HeapByteBuffer   - java堆内存, 读写效率较低,受到GC影响
        class java.nio.DirectByteBuffer - 直接内存,读写效率较高(少一次数据拷贝), 不会受到GC影响
        */

    }
}
