package cn.itcast.netty.c4_网络编程;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

import static cn.itcast.netty.c2_bytebuffer.ByteBufferUtil.debugRead;

/**
 * 非阻塞模式
 * Created by John.Ma on 2021/10/4 0004 10:44
 */
@Slf4j
public class Server2 {
    public static void main(String[] args) throws IOException {
        //使用nio 来理解阻塞模式
        // 0.ByteBuffer
        ByteBuffer buffer = ByteBuffer.allocate(16);

        //1.创建了服务器
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);//非阻塞模式

        //2.绑定监听端口
        ssc.bind(new InetSocketAddress(8888));

        //3.连接集合
        List<SocketChannel> channels = new ArrayList<>();
        while(true) {
            //4.accept,建立与客户端连接, socketChannel与客户端之间通信
//            log.debug("connecting...");
            SocketChannel sc = ssc.accept(); // 编程非阻塞了,不会等待,没有连接返回null
            if(sc != null){
                log.debug("connected...{}",sc);
                sc.configureBlocking(false); // 将scoketChannel设置成非阻塞模式
                channels.add(sc);
            }
            for(SocketChannel channel: channels){
                // 5.接收客户端发送的数据
                //log.debug("before read...{}", channel);
                int read = channel.read(buffer);  //非阻塞,线程仍然会继续运行,如果没有读到数据,read返回0
                if(read > 0){
                    buffer.flip();
                    debugRead(buffer);
                    buffer.clear();
                    log.debug("after read...{}", channel);
                }

            }



        }
    }
}
