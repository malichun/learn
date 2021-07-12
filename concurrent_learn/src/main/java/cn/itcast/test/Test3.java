package cn.itcast.test;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

@Slf4j(topic= "c.Test3")
public class Test3 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        FutureTask<Integer> task = new FutureTask<>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                log.debug("线程执行!");
                Thread.sleep(1000);  // 模拟过一段时间以后返回结果
                return 100;
            }
        });

        Thread r1 = new Thread(task,"t2");
        r1.start();

        // 获取线程中方法执行后的返回结果
        log.debug("{}",task.get());

    }



}

