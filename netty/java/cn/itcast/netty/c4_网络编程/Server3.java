package cn.itcast.netty.c4_网络编程;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static cn.itcast.netty.c2_bytebuffer.ByteBufferUtil.debugAll;
import static cn.itcast.netty.c2_bytebuffer.ByteBufferUtil.debugRead;

/**
 * Selector
 * Created by John.Ma on 2021/10/4 0004 10:44
 */
@Slf4j
public class Server3 {
    private static void split(ByteBuffer source){
        source.flip(); // 切换为读模式
        for (int i = 0; i < source.limit(); i++) {
            if(source.get(i) == '\n'){
                // 搜到了一条完整的消息
                if(source.get(i) == '\n'){
                    int length = i + 1 -source.position(); // 消息的长度
                    // 把这条消息存入新的ByteBuffer
                    ByteBuffer target = ByteBuffer.allocate(length);
                    // 从source 读,向target中写
                    for(int j = 0;j<length;j++){
                        target.put(source.get());
                    }
                    debugAll(target);
                }
            }
        }

        source.compact();
    }

    public static void main(String[] args) throws IOException {
        //1.创建 Selector, 管理多个多个channel
        Selector selector = Selector.open();
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);

        //2. 建立selector和channel之间的联系,(注册)
        // SelectionKey 就是事件发生后,通过它知道事件和哪个channel的事件
        SelectionKey sscKey = ssc.register(selector, 0, null);
        // key只关注accept事件
        sscKey.interestOps(SelectionKey.OP_ACCEPT); // 感兴趣
        log.debug("register ke:{}",sscKey);

        ssc.bind(new InetSocketAddress(8888));

        while(true) {
            // 3.selector的select,没有事件,线程阻塞,有事件发生,线程才会恢复运行
            // select在事件未处理时不会阻塞, 事件发生后,要么处理,要么取消,不能置之不理
            selector.select();

            // 4.处理事件, selectionKeys内部包含了所有发生的事件
            Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
            while(iter.hasNext()){
                SelectionKey key = iter.next();
                // 处理key的时候,要从SelectedKeys 集合中移除, 否自下次会空指针异常
                iter.remove();

                log.debug("key: {}",key);

                // 区分事件类型
                if (key.isAcceptable()) { // 如果是accept
                    ServerSocketChannel channel = (ServerSocketChannel)key.channel();
                    SocketChannel sc = channel.accept();

                    sc.configureBlocking(false); // 设置非阻塞模式
                    // 连接已建立


                    ByteBuffer buffer = ByteBuffer.allocate(16);

                    // 第三个参数是附件 attachment,将byteBuffer作为附件关联到SelectionKey中,让每个socketChannel对应一个Buffer
                    SelectionKey scKey = sc.register(selector, 0, buffer);
                    scKey.interestOps(SelectionKey.OP_READ); // 关注读事件
                    log.debug("{}",sc);

                }else if(key.isReadable()){ // 如果是 read
                    try { // 防止客户端关掉,服务端出现异常
                        SocketChannel channel = (SocketChannel)key.channel(); // 拿到触发时间的channel

                        // 从key里面拿到上面定义好的附件(buffer)
                        ByteBuffer buffer = (ByteBuffer)key.attachment();
                        int read = channel.read(buffer); // 如果是正常断开,返回值是-1
                        if(read == -1){
                            key.cancel();
                        }else {
                            // buffer.flip();
                            // debugRead(buffer);
                            // 替换,防止粘包,半包问题
                            split(buffer);
                            // 一个内容都没有被读取掉,需要扩容
                            if(buffer.position() == buffer.limit()){
                                ByteBuffer newBuffer = ByteBuffer.allocate(buffer.capacity()*2);
                                buffer.flip(); // 切换成读模式
                                newBuffer.put(buffer);
                                // 重新更新扩容后的附件
                                key.attach(newBuffer);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        key.cancel(); // 因为客户端断开了,key需要取消,(处理或者取消,不能不做),从selector的Key集合中真正删除
                    }
                }


                // 或者
//                key.cancel(); // 这个是取消,可以不处理,但是要取消
            }


        }
    }
}
