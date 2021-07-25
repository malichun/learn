package cn.itcast.n8;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * Created by John.Ma on 2021/7/25 0025 19:13
 */
@Slf4j(topic = "c.TestCyclicBarrier")
public class TestCyclicBarrier {
    public static void main(String[] args) {
        ExecutorService service = Executors.newFixedThreadPool(2);
        CyclicBarrier barrier = new CyclicBarrier(2,()->{ // 第三个任务,当其他两个任务都执完成了,这个会质心
            log.debug("task1 task2 finish ....");
        });
        for (int i = 0; i < 3; i++) {
            service.submit(()-> {
                log.debug("task1 begin...");
                sleep(1);
                try {
                    barrier.await();// 2 -1 =0
//                log.debug("task1 end...");
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            });

            service.submit(()-> {
                log.debug("task2 begin...");
                sleep(1);
                try {
                    barrier.await();// 1-1=0
//                log.debug("task2 end...");
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            });
        }

        service.shutdown();
    }

    private static void sleep(int i) {
        try {
            Thread.sleep(i * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // 多个CountDownLatch
    private static void test1(){
        ExecutorService service = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 3; i++) {
            CountDownLatch latch = new CountDownLatch(2);
            service.submit(() -> {
                log.debug("task1 start...");
                latch.countDown();;
            });
            service.submit(() -> {
                log.debug("task2 start...");
                latch.countDown();;
            });
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.debug("task1 task2 finish...");
        }

        service.shutdown();
    }


}
