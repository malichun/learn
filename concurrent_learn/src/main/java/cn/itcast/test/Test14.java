package cn.itcast.test;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * Created by John.Ma on 2021/7/8 0008 20:27
 */
@Slf4j(topic = "c.Test14")
public class Test14 {
    public static void main(String[] args) throws InterruptedException {
        test3();
    }

    // 打断park线程,不会清空打断状态
    public static void test3() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            log.debug("park...");
            LockSupport.park(); // 线程t1被park了,外面没有interrupt(),会hang在这儿
            log.debug("unpark...");
            log.debug("打断状态: {}", Thread.currentThread().isInterrupted());
        },"t1");
        t1.start();

        TimeUnit.SECONDS.sleep(1);
        log.debug("打断t1前的状态{}",t1.isInterrupted());
        System.out.println(t1.getState());
        t1.interrupt(); // 打断线程t1
    }

    // 如果打断标记已经是true了,则park会失效
    public static void test4() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            for(int i =0;i<5;i++){
                log.debug("park");
                LockSupport.park(); // 第一次通过外面的interrupt()方法打断后,后面几次是true会失效
                log.debug("打断状态: {}",Thread.currentThread().isInterrupted());
            }
        },"t1");

        t1.start();


        Thread.sleep(1);
        t1.interrupt();
    }

}
