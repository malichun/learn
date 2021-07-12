package cn.itcast.test;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by John.Ma on 2021/7/8 0008 0:22
 */
// 打断正常运行的线程
@Slf4j(topic = "c.Test12")
public class Test12 {
    public static void main(String[] args) throws InterruptedException{
        Thread t1 = new Thread(() -> {
            while(true){
                boolean interrupted = Thread.currentThread().isInterrupted(); // 自己判断打断标记,自己停止吧
                if(interrupted){
                    log.debug("被打断了,退出循环");
                    break;
                }
            }
        }, "t1");
        t1.start();
        Thread.sleep(1000); // 主线程停1秒
        log.debug("interrupt...");
        t1.interrupt();
    }
}
