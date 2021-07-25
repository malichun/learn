package cn.itcast.n8;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by John.Ma on 2021/7/25 0025 15:49
 */
@Slf4j(topic = "c.TestCountdownLatch")
public class TestCountdownLatch {
    public static void main(String[] args) throws InterruptedException {


    }

    /**
     * 应用之同步等待多线程准备完毕
     */
    private static void test3() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(10);
        ExecutorService service = Executors.newFixedThreadPool(10);
        String[] all = new String[10];
        Random r = new Random();
        for (int j = 0; j < 10; j++) {
            int k = j;
            service.submit(() -> {

                for (int i = 0; i <= 100; i++) {
                    all[k] = i + "%";
                    try {
                        Thread.sleep(r.nextInt(100));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.print("\r" + Arrays.toString(all));
                }
                latch.countDown();
            });
        }


        latch.await();
        System.out.println();
        System.out.println("游戏开始...");

        service.shutdown();
    }

    // 使用多线程优化倒计时锁
    private static void test2() {
        // 倒计时锁
        CountDownLatch latch = new CountDownLatch(3);

        // 2.用线程池改进
        ExecutorService service = Executors.newFixedThreadPool(4);
        service.submit(() -> {
            log.debug("begin....");
            sleep(1);
            latch.countDown();
            log.debug("end... {}",latch.getCount());
        });

        service.submit(() -> {
            log.debug("begin....");
            sleep(1.5);
            latch.countDown();
            log.debug("end... {}",latch.getCount());
        });

        service.submit(() -> {
            log.debug("begin....");
            sleep(2);
            latch.countDown();
            log.debug("end... {}",latch.getCount());
        });

        service.submit(() -> {
            try {
                log.debug("waiting...");
                latch.await(); // 计数没有减为0则阻塞
                log.debug("waiting end ...");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    private static void test1() throws InterruptedException {
        // 倒计时锁
        CountDownLatch latch = new CountDownLatch(3);
        new Thread(() ->{
            log.debug("begin...");
            sleep(1);
            latch.countDown();
            log.debug("finish ...{}",latch.getCount());
        }).start();

        new Thread(() ->{
            log.debug("begin...");
            sleep(1.5);
            latch.countDown();
            log.debug("finish ...");
        }).start();

        new Thread(() ->{
            log.debug("begin...");
            sleep(2);
            latch.countDown();
            log.debug("finish ...");
        }).start();


        log.debug("主线程waiting...");
        latch.await(); // 计数没有减为0则阻塞
        log.debug("主线程等待结束...");
    }

    private static void sleep(double seconds){
        try {
            Thread.sleep((long)(1000 * seconds));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
