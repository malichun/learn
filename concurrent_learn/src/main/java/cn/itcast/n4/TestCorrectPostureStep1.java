package cn.itcast.n4;

import lombok.extern.slf4j.Slf4j;

/**
 * @description:
 * @author: malichun
 * @time: 2021/7/12/0012 18:03
 */
@Slf4j(topic = "c.TestCorrectPostureStep1")
public class TestCorrectPostureStep1 {
    static final Object room = new Object();
    static boolean hasCigarette = false; // 有没有烟
    static boolean hasTakeout = false;

    public static void main(String[] args) throws InterruptedException {
        new Thread(() -> {
            synchronized (room) {
                log.debug("有烟没?[{}]", hasCigarette);
                if (!hasCigarette) {
                    log.debug("没烟,先歇会!");
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.debug("有烟没?[{}]", hasCigarette);
                if (hasCigarette) {
                    log.debug("可以开始干活了");
                }
            }
        }, "小南").start();

        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                synchronized (room) {
                    log.debug("可以开始干活了");
                }
            }, "其他人").start();
        }

        Thread.sleep(1000);
        new Thread(() -> {
            // 这里能不能加synchronized(room)?
            hasCigarette = true;
            log.debug("烟到了噢!");
        }, "送烟的").start();
    }

}
