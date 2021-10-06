package cn.itcast.netty.c4_网络编程;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static cn.itcast.netty.c2_bytebuffer.ByteBufferUtil.debugAll;

/**
 * Created by John.Ma on 2021/10/5 0005 22:47
 */
@Slf4j
public class MultiThreadServer {
    public static void main(String[] args) throws IOException {
        Thread.currentThread().setName("boss");
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        Selector boss = Selector.open();
        SelectionKey bossKey = ssc.register(boss,0,null);
        bossKey.interestOps(SelectionKey.OP_ACCEPT);
        ssc.bind(new InetSocketAddress(8080));

        // // 1.创建固定数量的worker,并初始化
        // Worker worker = new Worker("worker-0");,可用的核心数
        Worker[] workers = new Worker[Runtime.getRuntime().availableProcessors()];
        for (int i = 0; i < workers.length; i++) {
            workers[i] = new Worker("worker-"+i);
        }
        AtomicInteger index = new AtomicInteger(); // 计数器
        while(true){
            boss.select(); // 没有事件就阻塞
            Iterator<SelectionKey> iter = boss.selectedKeys().iterator();
            while(iter.hasNext()){
                SelectionKey key = iter.next();
                iter.remove();
                if(key.isAcceptable()){
                    SocketChannel sc = ssc.accept();
                    sc.configureBlocking(false);
                    log.debug("connected...{}",sc.getRemoteAddress());
                    // 2.关联 worker 的 selector
                    log.debug("before register...{}",sc.getRemoteAddress());
                    // worker.register(sc); //boss线程调用!! 初始化 selector ,启动work导致阻塞(执行了Selector.select()方法)
                    // 上面换成round robin 算法
                    workers[index.getAndIncrement() % workers.length].register(sc);
                    log.debug("after register...{}",sc.getRemoteAddress());
                }
            }
        }
    }

    /**
     * 监测读写事件
     */
    static class Worker implements Runnable{
        private Thread thread;
        private Selector selector;
        private String name;
        private volatile boolean start = false;// 还没有初始化
        // 使用一个队列
        private ConcurrentLinkedQueue<Runnable> queue = new ConcurrentLinkedQueue<>();

        public Worker(String name) {
            this.name = name;
        }

        /**
         * 初始化线程,和selector
         */
        public void register(SocketChannel sc) throws IOException {
            if(!start) {
                selector = Selector.open();
                thread = new Thread(this, name);
                thread.start();
                start = true;
            }
            // 方法一: 多个线程共享变量的方法
        /*
            // 向队列添加了任务,但这个任务并没有立即执行
            queue.add(() -> {
                // 每次都要执行
                try {
                    sc.register(selector, SelectionKey.OP_READ);
                } catch (ClosedChannelException e) {
                    e.printStackTrace();
                }
            });
            // 调用selector的wakeUp
            selector.wakeup(); // 唤醒 select 方法

         */
        selector.wakeup();
        sc.register(selector,SelectionKey.OP_READ,null); //

        }

        @Override
        public void run() {
            while(true){
                try {
                    selector.select(); // worker-0 阻塞, wakeup
                    // 方式一: 使用队列
                    /*
                    // Runnable task = queue.poll();
                    // if(task!=null){
                    //     task.run(); // 执行了注册 sc.register(selector, SelectionKey.OP_READ);
                    // }
                    */
                    Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
                    while(iter.hasNext()){
                        SelectionKey key = iter.next();
                        iter.remove();
                        if (key.isReadable()) { // 可读
                            ByteBuffer buffer = ByteBuffer.allocate(16);
                            SocketChannel channel = (SocketChannel) key.channel();
                            log.debug("read...{}",channel.getRemoteAddress());
                            channel.read(buffer);
                            buffer.flip();
                            debugAll(buffer);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
