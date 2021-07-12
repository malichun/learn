package cn.itcast.test;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by John.Ma on 2021/7/7 0007 22:46
 */
@Slf4j(topic = "c.Test10")
public class Test10 {
    static int r = 0;
    public static void main(String[] args) throws InterruptedException {
        test1();
    }

    private static void test1() throws InterruptedException{
        log.debug("开始");
        Thread t1 = new Thread(() -> {
            log.debug("开始");
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.debug("结束");
            r = 10;
        });
        t1.start();
        t1.join();
        log.debug("结果为: {}", r);
        log.debug("结束");

    }
}
