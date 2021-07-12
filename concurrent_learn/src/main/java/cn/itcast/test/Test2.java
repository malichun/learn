package cn.itcast.test;


import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "c.Test2")
public class Test2 {
    public static void main(String[] args) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                log.debug("running");
            }
        };

        Runnable r2 = () -> log.debug("running");

        new Thread(r,"t2").start();

        new Thread(() -> log.debug("running"), "thread-1").start();
    }
}
