package cn.itcast.test;

import lombok.extern.slf4j.Slf4j;

/**
 * @description:
 * @author: malichun
 * @time: 2021/7/12/0012 17:25
 */
@Slf4j(topic = "c.Test18")
public class Test18 {
    static final Object lock = new Object();

    public static void main(String[] args) throws InterruptedException {
        synchronized (lock){
            lock.wait(); // 主线程等待状态
        }

    }

}
