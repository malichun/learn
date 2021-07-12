package cn.itcast.test;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by John.Ma on 2021/7/7 0007 21:57
 */
@Slf4j(topic = "c.Test6")
public class Test6 {
    public static void main(String[] args) throws InterruptedException {

        Thread t1 = new Thread("t1") {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        log.debug("t1 state {}", t1.getState());
        t1.start();
        log.debug("t1 state {}", t1.getState());
        Thread.sleep(1000);
        log.debug("t1.state {} " ,t1.getState());
    }
}
