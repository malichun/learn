package cn.itcast.n8;

import lombok.extern.slf4j.Slf4j;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by John.Ma on 2021/7/23 0023 6:06
 */
@Slf4j(topic = "c.TestTimer")
public class TestTimer {
    public static void main(String[] args) {


    }

    private static void method2() {
        ScheduledExecutorService pool = Executors.newScheduledThreadPool(2);
        log.debug("start....");
        pool.scheduleAtFixedRate(() ->{
            log.debug("running1");
            sleep(3);
        },1,1, TimeUnit.SECONDS);
        pool.scheduleAtFixedRate(() ->{
            log.debug("running2");
            sleep(3);
        },1,1,TimeUnit.SECONDS);
        pool.scheduleAtFixedRate(() ->{
            log.debug("running3");
            sleep(3);
        },1,1,TimeUnit.SECONDS);
    }

    /**
     * 延时执行任务
     */
    private static void method() {
        // 使用任务调度线程池
        ScheduledExecutorService pool = Executors.newScheduledThreadPool(2);
        pool.schedule(() -> {
            log.debug("task1");
            sleep(2);
        },1, TimeUnit.SECONDS); // 1秒后执行
        pool.schedule(() -> {
            log.debug("task2");
        },1,TimeUnit.SECONDS);

        log.debug("start...");
    }

    // 使用timer
    private static void test1() {
        Timer timer = new Timer();
        TimerTask task1 = new TimerTask() {
            @Override
            public void run() {
                log.debug("task 1");
                sleep(2);
            }
        };
        TimerTask task2 = new TimerTask() {
            @Override
            public void run() {
                log.debug("task 2");
            }
        };

        log.debug("start ....");
        timer.schedule(task1,1000);
        timer.schedule(task2,1000);
    }


    private static void sleep(long seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
