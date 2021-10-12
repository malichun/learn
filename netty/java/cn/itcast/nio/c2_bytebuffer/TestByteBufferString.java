package cn.itcast.nio.c2_bytebuffer;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static cn.itcast.nio.c2_bytebuffer.ByteBufferUtil.debugAll;

/**
 * 编码, 字符串
 * Created by John.Ma on 2021/10/3 0003 16:30
 */
public class TestByteBufferString {
    public static void main(String[] args) {
        // 1.字符串转为ByteBuffer
        ByteBuffer buffer1 = ByteBuffer.allocate(16);
        buffer1.put("hello".getBytes());
        debugAll(buffer1);

        // 2.Charset,自动切换读模式
        ByteBuffer buffer2 = StandardCharsets.UTF_8.encode("hello");
        debugAll(buffer2);

        // 3.wrap,自动切换读模式
        ByteBuffer buffer3 = ByteBuffer.wrap("hello".getBytes());
        debugAll(buffer3);

        String str1 = StandardCharsets.UTF_8.decode(buffer2).toString();
        System.out.println(str1);

        buffer1.flip();
        String str = StandardCharsets.UTF_8.decode(buffer1).toString();
        System.out.println(str);

    }
}
