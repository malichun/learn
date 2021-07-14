package cn.itcast.n4;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by John.Ma on 2021/7/14 0014 22:54
 */
@Slf4j(topic = "c.TestLiveLock")
public class TestLiveLock { // 活锁
    static volatile int count = 10;
    static final Object lock = new Object();

    public static void main(String[] args) {
        new Thread(() ->{
            // 期望减到0退出循环
            while(count < 20){

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                count++;
                log.debug("count: {}",count);
            }
        },"t1").start();

        new Thread(() ->{
            // 期望减到0退出循环
            while(count > 0){

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                count--;
                log.debug("count: {}",count);
            }
        },"t1").start();


    }
}
