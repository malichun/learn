package cn.itcast.n4.deadlock.v1;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by John.Ma on 2021/7/14 0014 22:29
 */
public class TestDeadLock {
    public static void main(String[] args) {
        // 就餐
        Chopstick c1 = new Chopstick("1");
        Chopstick c2 = new Chopstick("2");
        Chopstick c3 = new Chopstick("3");
        Chopstick c4 = new Chopstick("4");
        Chopstick c5 = new Chopstick("5");

        new Philosopher("苏格拉底", c1, c2).start();
        new Philosopher("柏拉图", c2, c3).start();
        new Philosopher("亚里士多德", c3, c4).start();
        new Philosopher("赫拉克利特", c4, c5).start();
        new Philosopher("阿基米德", c5, c1).start();


    }

}

// 筷子
class Chopstick extends ReentrantLock { // 每个筷子继承可重入锁
    String name;

    public Chopstick(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "筷子{" + name + "}";
    }
}

// 哲学家
@Slf4j(topic = "c.Philosopher")
class Philosopher extends Thread {
    Chopstick left;
    Chopstick right;

    public Philosopher(String name, Chopstick left, Chopstick right) {
        super(name); // 将线程名字设置为name
        this.left = left;
        this.right = right;
    }

    private void eat() {
        log.debug("eating...");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        // 会发生死锁
//        while(true){
//            // 获得左手筷子
//            synchronized (left){
//                // 获得右手筷子
//                synchronized (right){
//                    // 吃饭
//                    eat();
//                }
//                // 放下右手筷子
//            }
//            // 放下左手筷子
//        }
        while (true) {
            // 尝试获得左手筷子
            if(left.tryLock()){ // 拿到锁才会进去
                try{
                    // 尝试获得右筷子
                    if(right.tryLock()){ // 拿到锁才会进去
                        try{
                            eat();
                        }finally {
                            right.unlock();
                        }
                    }
                }finally {
                    left.unlock(); // 释放自己手里的左手的筷子
                }
            }

        }
    }
}
