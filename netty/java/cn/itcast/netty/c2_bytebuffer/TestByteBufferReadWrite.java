package cn.itcast.netty.c2_bytebuffer;

import java.nio.ByteBuffer;

import static cn.itcast.netty.c2_bytebuffer.ByteBufferUtil.debugAll;

/**
 * @author ：malichun
 * @date 2021/9/30 11:36 上午
 * @description：
 * @modified By：
 */
public class TestByteBufferReadWrite {
    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        debugAll(buffer);

        buffer.put((byte)0x61); // 'a'
        debugAll(buffer);
        buffer.put(new byte[]{0x62, 0x63, 0x64}); // 写入b, c, d

        debugAll(buffer);


//        System.out.println(buffer.get()); //没有flip读不到东西
        buffer.flip();
        System.out.println(buffer.get());
        debugAll(buffer);

        buffer.compact();
        debugAll(buffer);


        buffer.put(new byte[]{0x65, 0x66}); // 写入e,f
        debugAll(buffer);
    }
}
