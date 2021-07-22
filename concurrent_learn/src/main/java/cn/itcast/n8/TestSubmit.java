package cn.itcast.n8;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.concurrent.*;

/**
 * Created by John.Ma on 2021/7/22 0022 22:17
 */
@Slf4j(topic = "c.TestSubmit")
public class TestSubmit {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(2);
        pool.invokeAll(new ArrayList<Callable<String>>(){{
            add(new Callable() {
                @Override
                public String call() throws Exception {
                    return "abc";
                }
            });
        }});


    }



    /**
     * 测试Callable 接口
     */
    public static void test1() throws ExecutionException, InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(2);
        Future<String> future =pool.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                log.debug("running");
                Thread.sleep(1000);
                return "ok";
            }
        });

        log.debug("{}",future.get()); // get这边先wait,线程执行完成会唤醒get线程
    }
}
