package cn.itcast.n8;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by John.Ma on 2021/7/22 0022 21:53
 */
@Slf4j(topic = "c.TestExecutors")
public class TestExecutors {
    public static void main(String[] args) {
        test2();
    }

    public static void test2(){
        ExecutorService pool = Executors.newSingleThreadExecutor();
        pool.execute(()-> {
            log.debug("1");
            int i = 1 /0;
        });

        pool.execute(() -> {
            log.debug("2");
        });

        pool.execute(() -> {
            log.debug("3");
        });
    }

}
