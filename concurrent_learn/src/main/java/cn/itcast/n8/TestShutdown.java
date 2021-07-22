package cn.itcast.n8;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by John.Ma on 2021/7/22 0022 23:10
 */
@Slf4j(topic = "c.TestShutdown")
public class TestShutdown {
    public static void main(String[] args) throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(2);

        Future<Integer> result1= pool.submit(() -> {
            log.debug("task 1 running ...");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.debug("task 1 finish...");
            return 1;
        });

        Future<Integer> result2= pool.submit(() -> {
            log.debug("task 2 running ...");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.debug("task 2 finish...");
            return 2;
        });

        Future<Integer> result3= pool.submit(() -> {
            log.debug("task 3 running ...");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.debug("task 3 finish...");
            return 3;
        });

        log.debug("shutdown");
        pool.shutdown();
        pool.awaitTermination(3, TimeUnit.SECONDS); // 等待3秒后执行主线程下面的代码
        log.debug("other"); // 不会等待

    }
}
