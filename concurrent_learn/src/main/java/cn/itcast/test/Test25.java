package cn.itcast.test;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.LockSupport;

/**
 * @description:
 * @author: malichun
 * @time: 2021/7/15/0015 16:46
 * 2个线程,先打印2,再打印1
 */
@Slf4j(topic = "c.Test25")
public class Test25 {
    static final Object lock = new Object(); // 使用同步
    // t2是否运行过,标志位
    static boolean t2runed = false;

    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            synchronized (lock) {
                // 如果t2没有执行过
                while (!t2runed) {
                    try {
                        // t1先等等,让出锁
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                // 当条件满足
                log.debug("1");
            }
        }, "t1");

        Thread t2 = new Thread(() -> {
            log.debug("2");
            synchronized (lock){
                // 修改运行标记
                t2runed = true;
                // 通知lock上等待的线程
                lock.notifyAll();
            }
        }, "t2");

        t1.start();
        t2.start();

    }
}

/**
 * 使用park,unpark解决
 * 先2后1
 */
@Slf4j(topic = "c.Test25_park_unpark")
class Test25_park_unpark{
    public static void main(String[] args) {
        Thread t1 = new Thread(() ->{
            LockSupport.park();
            log.debug("1");
        },"t1");

        Thread t2 = new Thread(() ->{
            log.debug("2");
            LockSupport.unpark(t1);
        },"t2");


        t1.start();
        t2.start();

    }
}
