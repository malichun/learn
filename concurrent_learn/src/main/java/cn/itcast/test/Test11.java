package cn.itcast.test;


import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j(topic = "c.Test11")
public class Test11 {
    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            log.debug("sleep");
            try {
                Thread.sleep(5000); // wait,join,sleep 会把打断标记清空,导致打断标记为假
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        t1.start();
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.debug("interrupt...");
        t1.interrupt();
        log.debug(" 打断状态: {}", t1.isInterrupted()); // 如果打断sleep,wait,join的线程,即使打断了,标记也为false
    }
}
