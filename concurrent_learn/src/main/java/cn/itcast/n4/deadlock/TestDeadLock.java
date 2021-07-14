package cn.itcast.n4.deadlock;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by John.Ma on 2021/7/14 0014 21:54
 */
@Slf4j(topic = "c.TestDeadLock")
public class TestDeadLock {

    public static void main(String[] args) {

        test1();

    }

    private static void test1(){
        Object A = new Object();
        Object B = new Object();
        Thread t1 = new Thread(() ->{
            synchronized (A){
                log.debug("lock A");
                sleep(1);
                synchronized (B){
                    log.debug("lock B");
                    log.debug("操作....");
                }
            }
        },"t1");

        Thread t2 = new Thread(() -> {
            synchronized (B){
                log.debug("lock B");
                synchronized (A){
                    log.debug("lock A");
                    log.debug("操作....");
                }
            }
        },"t2");

        t1.start();
        t2.start();
    }

    private static void sleep(long t){
        try {
            Thread.sleep(1000 * t);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
