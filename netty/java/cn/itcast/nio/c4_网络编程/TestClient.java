package cn.itcast.nio.c4_网络编程;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

/**
 * Created by John.Ma on 2021/10/5 0005 23:17
 */
public class TestClient {
    public static void main(String[] args) throws IOException {
        SocketChannel sc = SocketChannel.open();
        sc.connect(new InetSocketAddress("localhost",8080));
        sc.write(Charset.defaultCharset().encode("123456789abcdef"));
        System.in.read();
    }
}
