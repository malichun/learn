package cn.itcast.test;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @description: 测试送烟和外卖, 使用ReentrantLock实现
 * @author: malichun
 * @time: 2021/7/12/0012 18:03
 */
@Slf4j(topic = "c.Test24")
public class Test24 {
    static ReentrantLock ROOM = new ReentrantLock();
    static boolean hasCigarette = false;
    static boolean hasTakeout = false;
    // 开多间休息室
    // 等待烟的休息室
    static Condition waitCigaretteSet = ROOM.newCondition();
    // 等外卖的休息室
    static Condition waitTakeoutSet = ROOM.newCondition();

    public static void main(String[] args) throws InterruptedException {

        new Thread(() -> {
            ROOM.lock();
            try {
                log.debug("有烟没[{}]", hasCigarette);
                while (!hasCigarette) {
                    log.debug("没烟,先歇会!");
                    try {
                        waitCigaretteSet.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.debug("可以开始干活了");

            } finally {
                ROOM.unlock();// 解锁
            }

        }, "小南").start();

        new Thread(() -> {
            ROOM.lock();
            try {
                log.debug("外卖送到了没?[{}]", hasTakeout);
                while (!hasTakeout) {
                    log.debug("没外卖,先歇会!");
                    try {
                        waitTakeoutSet.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.debug("可以开始干活了");
            } finally {
                ROOM.unlock();
            }


        }, "小女").start();

        Thread.sleep(1000);
        new Thread(() -> {
            ROOM.lock();
            try {
                log.info("外卖送到了...");
                hasTakeout = true;
                waitTakeoutSet.signal(); // 一定要在有锁的情况下
            } finally {
                ROOM.unlock();
            }

        }, "送外卖的").start();

        Thread.sleep(1000);

        new Thread(() -> {
            ROOM.lock();
            try {
                log.info("烟送到了...");
                hasCigarette = true;
                waitCigaretteSet.signal();
            } finally {
                ROOM.unlock();
            }
        }, "送烟的").start();
    }

}
