package cn.itcast.nio.c4_网络编程;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Created by John.Ma on 2021/10/5 0005 18:15
 */
public class WriteClient {
    public static void main(String[] args) throws IOException {
        SocketChannel sc = SocketChannel.open();
        sc.connect(new InetSocketAddress("localhost",8080));

        //3.接收数据
        int count =0;
        while(true){
            ByteBuffer buffer = ByteBuffer.allocate(1024*1024);
            count += sc.read(buffer);
            System.out.println(count);
            buffer.clear();
        }

    }
}
