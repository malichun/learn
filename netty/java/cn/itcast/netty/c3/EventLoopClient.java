package cn.itcast.netty.c3;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

import java.net.InetSocketAddress;

/**
 * Created by John.Ma on 2021/10/6 0006 20:06
 */
public class EventLoopClient {
    public static void main(String[] args) throws InterruptedException {
        // 2. 带有Future,Promise的类型都是和 异步方法配套使用,用来处理结果
        ChannelFuture channelFuture = new Bootstrap()
            .group(new NioEventLoopGroup())
            .channel(NioSocketChannel.class)
            .handler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new StringEncoder());
                }
            })
            // 1. 连接到服务器
            // 异步非阻塞, main 发起了调用, 真正执行连接的connect 是 nio线程
            .connect(new InetSocketAddress("localhost", 8080));

        // 2.1 使用sync 方法来同步处理结果
         channelFuture.sync(); // 阻塞住当前线程, 知道Nio线程连接建立完毕

         // 无阻塞向下执行获取 channel
        Channel channel = channelFuture.channel();
        // 2. 向服务器发送数据
        channel.writeAndFlush("hello, world");

    }
}
