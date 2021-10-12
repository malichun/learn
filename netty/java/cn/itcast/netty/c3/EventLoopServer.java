package cn.itcast.netty.c3;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;

/**
 * Created by John.Ma on 2021/10/7 0007 17:21
 */
@Slf4j
public class EventLoopServer {
    public static void main(String[] args) {
        // 细分2: 创意一个而独立的EventLoopGroup
        EventLoopGroup group = new DefaultEventLoop(); // 只处理普通任务和定时任务

        new ServerBootstrap()
            // .group(new NioEventLoopGroup()) // 换成下面

            //public ServerBootstrap group(EventLoopGroup parentGroup, EventLoopGroup childGroup) {
            // boss 只负责 ServerScocketChannel 上accept事件 和worker 只负责socket channel上面的读写
            .group(new NioEventLoopGroup(), new NioEventLoopGroup(2))
            .channel(NioServerSocketChannel.class)
            .childHandler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {
                    ch.pipeline().addLast("handle1",new ChannelInboundHandlerAdapter(){
                        @Override                                         // ByteBuf
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                            final ByteBuf buf = (ByteBuf) msg;
                            log.debug(buf.toString(Charset.defaultCharset()));
                            ctx.fireChannelRead(msg); // 将消息传递给下一个handler
                        }
                    }).addLast(group, "handler2", new ChannelInboundHandlerAdapter(){
                        @Override                                         // ByteBuf
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                            final ByteBuf buf = (ByteBuf) msg;
                            log.debug(buf.toString(Charset.defaultCharset()));
                        }
                    });

                }
            })
            .bind(8080);
    }
}
