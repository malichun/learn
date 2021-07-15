package cn.itcast.test;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.ReentrantLock;

import static cn.itcast.test.Utils.sleep;

/**
 * Created by John.Ma on 2021/7/15 0015 0:55
 */
// 测试锁超时
@Slf4j(topic = "c.Test22_reentrant")
public class Test22_reentrantLock_suochaoshi {

    public static void main(String[] args) {
        ReentrantLock lock = new ReentrantLock();
        Thread t1 = new Thread(() -> {
            log.debug("启动,尝试获得锁...");
            if(!lock.tryLock()){
                log.debug("获取立刻失败,返回");
                return;
            }
            try{
                log.debug("获得了锁"); // 临界区代码
            }finally {
                lock.unlock();
            }
        },"t1");

        lock.lock();
        log.debug("主线程获得了锁,主线程继续往下执行");

        t1.start();

        try{
            sleep(2);
        }finally {
            lock.unlock();
        }

    }
}
