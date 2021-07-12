package cn.itcast.n4;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by John.Ma on 2021/7/10 0010 8:49
 */
@Slf4j(topic = "c.Test8Locks")
public class Test8Locks {

    public static void main(String[] args) {

        Number n1 = new Number();
        Number n2 = new Number();
        new Thread(() -> {
            log.debug("a.begin");
            n1.a();
        }).start();

        new Thread(() -> {
            log.debug("b.begin");
            n2.b();
        }).start();

    }
}

@Slf4j(topic = "c.Number")
class Number {
    public synchronized void a() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.debug("1");
    }

    public synchronized void b() {
        log.debug("2");
    }

}



