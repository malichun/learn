package cn.itcast.test;

import lombok.extern.slf4j.Slf4j;

/**
 * @description:
 * @author: malichun
 * @time: 2021/7/12/0012 17:54
 */
@Slf4j(topic = "c.Test19")
public class Test19 {

    static final Object lock = new Object();

    public static void main(String[] args) {
        new Thread(() ->{
            synchronized (lock){
                log.debug("获得锁");
//                try {
//                    Thread.sleep(20000); // sleep 不会释放锁
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                try {
                    lock.wait(2000); // wait会释放锁
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        synchronized (lock){
            log.debug("获得锁");
        }

    }
}
