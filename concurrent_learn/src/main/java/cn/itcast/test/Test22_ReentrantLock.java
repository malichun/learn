package cn.itcast.test;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by John.Ma on 2021/7/14 0014 23:46
 */
@Slf4j(topic = "c.Test22_ReentrantLock")
public class Test22_ReentrantLock {
    private static ReentrantLock lock = new ReentrantLock(); // 默认不公平锁

    public static void main(String[] args) throws InterruptedException {
//        m0(); // 测试可重入

        Thread t1 = new Thread(() -> {
            try {
                // 如果没有竞争,此方法就会获取lock对象锁
                // 如果有竞争就进入阻塞队列,可以被其他线程interrupt()方法打断
                log.debug("尝试获得锁");
                lock.lockInterruptibly();// 可打断

            } catch (InterruptedException e) {
                e.printStackTrace();
                log.debug("没有获得锁,返回");
                return;
            }

            try{
                log.debug("获取到锁");
            }finally{
                lock.unlock();
            }

        }, "t1");

        lock.lock(); // 主线程获取锁,t1就阻塞住了
        t1.start();

        sleep(1);
        log.debug("打断t1");
        t1.interrupt();

    }

    private static void sleep(long seconds){
        try {
            Thread.sleep(seconds* 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // 测试可重入
    public static void m0(){
        lock.lock();
        try{
            log.debug("enter m0");
            m1();
        }finally {
            lock.unlock();
        }
    }

    public static void m1(){
        lock.lock();
        try{
            log.debug("enter m1");
            m2();
        }finally {
            lock.unlock();
        }
    }

    public static void m2(){
        lock.lock();
        try{
            log.debug("enter m2");
        }finally {
            lock.unlock();
        }
    }
}
