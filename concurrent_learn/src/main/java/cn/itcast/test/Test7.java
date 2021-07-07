package cn.itcast.test;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by John.Ma on 2021/7/7 0007 22:04
 */
@Slf4j(topic = "c.Test7")
public class Test7 {
    public static void main(String[] args) {
        Thread t1 = new Thread("t1") {
            @Override
            public void run() {

                log.debug("enter sleep...");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    log.debug("wake up...");
                    e.printStackTrace();
                }
            }
        };

        t1.start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.debug("interrupt...");
        // 让t1打断
        t1.interrupt();
    }
}
