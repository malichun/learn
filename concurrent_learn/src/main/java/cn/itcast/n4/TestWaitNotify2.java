package cn.itcast.n4;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by John.Ma on 2021/7/13 0013 22:44
 */
@Slf4j(topic = "c.TestWaitNotify")
public class TestWaitNotify2 {
    final static Object obj = new Object();

    public static void main(String[] args) throws InterruptedException {
        new Thread(() -> {
            synchronized (obj){
                log.debug("执行....");
                try {
                    obj.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.debug("其他代码...."); // 断点
            }
        },"t1").start();

        new Thread(() ->{
            synchronized (obj){
                log.debug("执行....");
                try {
                    obj.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.debug("其他代码...."); // 断点
            }
        },"t2").start();

        Thread.sleep(500);
        log.debug("唤醒obj上其他线程");
        synchronized (obj){
            obj.notifyAll(); // 唤醒obj上所有等待的线程  断点
        }

    }
}
