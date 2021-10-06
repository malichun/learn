package cn.itcast.netty.c3_文件编程;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

/**
 * 集中写入
 * Created by John.Ma on 2021/10/3 0003 18:10
 */
public class TestGatheringWrite {
    public static void main(String[] args) {
        ByteBuffer b1 = StandardCharsets.UTF_8.encode("hello");
        ByteBuffer b2 = StandardCharsets.UTF_8.encode("world");
        ByteBuffer b3 = StandardCharsets.UTF_8.encode("你好"); // 6个字节
        //集中写入
        try(FileChannel channel = new RandomAccessFile("word2.txt", "rw").getChannel();){
            channel.write(new ByteBuffer[]{b1,b2,b3});
        }catch (IOException e){

        }


    }
}
