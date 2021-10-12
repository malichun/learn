package cn.itcast.nio.c4_网络编程;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

/**
 * Created by John.Ma on 2021/10/4 0004 11:07
 */
public class Client {
    public static void main(String[] args) throws IOException {
        SocketChannel sc = SocketChannel.open();
        sc.connect(new InetSocketAddress("localhost",8888));
        SocketAddress address = sc.getLocalAddress();
        // sc.write(Charset.defaultCharset().encode("hello\nworld\n"));
        sc.write(Charset.defaultCharset().encode("0123456789abcdef3333\n"));
        System.in.read();

        System.out.println("waiting....");
    }
}
