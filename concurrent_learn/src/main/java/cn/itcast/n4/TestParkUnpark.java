package cn.itcast.n4;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.LockSupport;

/**
 * @description:
 * @author: malichun
 * @time: 2021/7/13/0013 14:07
 */
@Slf4j(topic = "c.TestParkUnpark")
public class TestParkUnpark {
    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            log.debug("start....");
            sleep(2);
            log.debug("park....");
            LockSupport.park(); // 当前线程为wait状态
            log.debug("resume...");
        },"t1");
        t1.start();

        sleep(1);

        log.debug("unpark....");
        LockSupport.unpark(t1);
    }

    public static void sleep(long seconds){
        try {
            Thread.sleep(seconds*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
