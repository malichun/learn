package cn.itcast.nio.c3_文件编程;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;


import static cn.itcast.nio.c2_bytebuffer.ByteBufferUtil.debugAll;

/**
 * 分散读取
 * Created by John.Ma on 2021/10/3 0003 16:52
 */
public class TestScatteringReads {
    public static void main(String[] args) {
        String path = TestScatteringReads.class.getClassLoader().getResource("words.txt").getPath();
        try(FileChannel channel = new RandomAccessFile(path, "r").getChannel()){
            ByteBuffer b1 = ByteBuffer.allocate(3);
            ByteBuffer b2 = ByteBuffer.allocate(3);
            ByteBuffer b3 = ByteBuffer.allocate(5);
            channel.read(new ByteBuffer[]{b1, b2, b3});
            b1.flip();
            b2.flip();
            b3.flip();
            debugAll(b1);
            debugAll(b2);
            debugAll(b3);
        }catch (IOException e){

        }


    }
}
