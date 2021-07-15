package cn.itcast.test;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by John.Ma on 2021/7/14 0014 23:46
 */
@Slf4j(topic = "c.Test22_ReentrantLock")
public class Test22_ReentrantLock_kedaduan {
    private static ReentrantLock lock = new ReentrantLock();
    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            try {
                // 如果没有竞争,那么次方法就会获取lock对象锁
                // 如果有竞争就进入阻塞队列,可以被其他线程interrupt()打断
                log.debug("尝试获得锁");
                lock.lockInterruptibly(); // 可打断,后面不需要unlock,被动死等
            } catch (InterruptedException e) {
                e.printStackTrace();
                log.debug("没有获得锁,返回");
                return;
            }
            try{
                log.debug("获取到锁了");
            }finally {
                lock.unlock();
            }
        }, "t1");
        // 主线程先加的锁,t1线程进入阻塞,主线程继续往下执行
        lock.lock();

        t1.start();
        sleep(1);
        log.debug("打断 t1线程");
        t1.interrupt();
    }

    private static void sleep(long seconds){
        try {
            Thread.sleep(1000 * seconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
