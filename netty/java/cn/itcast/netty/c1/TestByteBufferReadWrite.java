package cn.itcast.netty.c1;

import java.nio.ByteBuffer;

import static cn.itcast.netty.c1.ByteBufferUtil.debugAll;

/**
 * @author ：malichun
 * @date 2021/9/30 11:36 上午
 * @description：
 * @modified By：
 */
public class TestByteBufferReadWrite {
    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(10);

        buffer.put((byte)0x61); // 'a'
        debugAll(buffer);

    }
}
