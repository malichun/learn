package cn.itcast.n8;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @description:
 * @author: malichun
 * @time: 2021/7/22/0022 10:31
 * 测试线程池
 */
@Slf4j(topic = "c.TestThreadPoolExecutors")
public class TestThreadPoolExecutors {
    public static void main(String[] args) {

        ExecutorService pool = Executors.newFixedThreadPool(2, new ThreadFactory() {

            private AtomicInteger t = new AtomicInteger(1);
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r,"mypool_t"+t.getAndIncrement());
            }
        }); // 固定大小的线程池

        pool.execute(() -> {
            log.debug("1");
        });
        pool.execute(() -> {
            log.debug("2");
        });
        pool.execute(() -> {
            log.debug("3");
        });


//        pool.shutdown();
    }
}
