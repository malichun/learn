package cn.itcast.test;

import lombok.extern.slf4j.Slf4j;

/**
 * @description:
 * @author: malichun
 * @time: 2021/7/16/0016 11:54
 */
@Slf4j(topic = "c.Test32")
public class Test32 {
    static boolean run = true;

    // 锁对象
    final static Object lock = new Object();

    public static void main(String[] arg) throws InterruptedException {

        Thread t = new Thread(() -> {
            while (run) {
                synchronized (lock) {
                    // ..
                    if (!run) {
                        break;
                    }
                }
            }

        });
        t.start();

        Thread.sleep(1000);
        log.debug("停止 t");
        synchronized (lock) {
            run = false;
        }
        System.out.println();

    }
}
