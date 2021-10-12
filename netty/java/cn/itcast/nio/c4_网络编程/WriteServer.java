package cn.itcast.nio.c4_网络编程;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;

/**
 * Created by John.Ma on 2021/10/5 0005 18:04
 */
public class WriteServer {
    public static void main(String[] args) throws IOException {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        ssc.bind(new InetSocketAddress(8080));

        Selector selector = Selector.open();
        ssc.register(selector, SelectionKey.OP_ACCEPT);


        while(true){
            selector.select();
            Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
            while(iter.hasNext()){
                SelectionKey key = iter.next();
                iter.remove();
                if (key.isAcceptable()) {
                    SocketChannel sc = ssc.accept(); // 只有1个,可以这么干
                    sc.configureBlocking(false);
                    // 向Selector中注册
                    SelectionKey scKey = sc.register(selector,0,null);
                    scKey.interestOps(SelectionKey.OP_READ); //

                    // 1.向客户端发送大量数据
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < 5000000; i++) {
                        sb.append("a");
                    }
                    ByteBuffer buffer = Charset.defaultCharset().encode(sb.toString());

                    // 2.返回值代表实际写入的字节数,不能保证一次数据都写入到客户端
                    int write = sc.write(buffer);
                    System.out.println(write);

                    // 3.判断是否有剩余内容
                    if(buffer.hasRemaining()){
                        // 4.关注可写事件 读事件1,写事件4
                        scKey.interestOps(scKey.interestOps() + SelectionKey.OP_WRITE);
                        // scKey.interestOps(scKey.interestOps() | SelectionKey.OP_WRITE);
                        // 5.未写完的数据挂到 scKey 上,附件
                        scKey.attach(buffer);
                    }
                }else if(key.isWritable()){
                    ByteBuffer buffer = (ByteBuffer)key.attachment();
                    SocketChannel sc = (SocketChannel) key.channel();
                    int write = sc.write(buffer);
                    System.out.println(write);
                    // 6.清理操作
                    if(!buffer.hasRemaining()){
                        key.attach(null); // 将附件清空,清除buffer
                        // 数据写完了,不需要关注可写事件了
                        key.interestOps(key.interestOps() - SelectionKey.OP_WRITE);
                    }

                }
            }
        }


    }
}
