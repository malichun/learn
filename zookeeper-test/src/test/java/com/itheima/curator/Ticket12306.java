package com.itheima.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.concurrent.TimeUnit;

/**
 * @author: malichun
 * @date 2022/4/18 0018 23:42
 */
public class Ticket12306 implements Runnable{

    private int tickets = 10; // 数据库的票数

    // 分布式锁, 可重入
    private InterProcessMutex lock;

    public Ticket12306() {
        // 重试策略
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(3000, 10);
        CuratorFramework client = CuratorFrameworkFactory.builder()
            .connectString("localhost:2181")
            .sessionTimeoutMs(60 * 1000)
            .connectionTimeoutMs(15 * 100)
            .retryPolicy(retryPolicy)
            .build();
        client.start();
        this.lock = new InterProcessMutex(client, "/lock");
    }

    @Override
    public void run() {
        while(true){
            // 获取锁
            try {
                lock.acquire(3, TimeUnit.SECONDS);
                if(tickets > 0){
                    System.out.println(Thread.currentThread()+":"+tickets);
                    tickets--;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                try {
                    lock.release();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
