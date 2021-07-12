package cn.itcast.test;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by John.Ma on 2021/7/8 0008 22:58
 */
@Slf4j(topic = "c.Test15")
public class Test15 {
    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() ->{
            while (true){
                if(Thread.currentThread().isInterrupted()){
                    break;
                }
            }
            log.debug("结束");
        },"t1");
        t1.start();

        Thread.sleep(1000);
        log.debug("结束");
    }
}
