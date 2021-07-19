package cn.itcast.test;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @description: 线程1输出 a 5次,线程2输出b 5次, 线程3输出c 5次, 现在要求输出 abcabcabcabcabc怎么实现
 * @author: malichun
 * @time: 2021/7/15/0015 17:17
 */
// 1.用wait notify实现
public class Test26_jiaotishuchu {
    static Thread t1;
    static Thread t2;
    static Thread t3;

    public static void main(String[] args) throws InterruptedException {
        // 1.方式1 :使用waitnotify实现
       /* WaitNotify wn = new WaitNotify(1, 5);
        new Thread(() -> wn.print("a", 1, 2), "t1").start();
        new Thread(() -> wn.print("b", 2, 3), "t2").start();
        new Thread(() -> wn.print("c", 3, 1), "t3").start();*/

        // 2.方式2.可重入锁
        /*AwaitSignal awaitSignal = new AwaitSignal(5);
        Condition a = awaitSignal.newCondition();
        Condition b = awaitSignal.newCondition();
        Condition c = awaitSignal.newCondition();
        new Thread(() -> awaitSignal.print("a",a,b),"t1").start();
        new Thread(() -> awaitSignal.print("b",b,c),"t2").start();
        new Thread(() -> awaitSignal.print("c",c,a),"t3").start();
        Thread.sleep(1000);
        // 发动,让a先启动
        awaitSignal.lock();
        try{
            a.signal();
        }finally {
            awaitSignal.unlock();
        }*/

        // 3.方式3.parkUnpark
        ParkUnpark pu = new ParkUnpark(5);
        t1 = new Thread(() -> pu.print("a",t2),"t1");
        t2 = new Thread(() -> pu.print("b",t3), "t2");
        t3 = new Thread(() -> pu.print("c",t1),"t1");

        t1.start();
        t2.start();
        t3.start();
        Thread.sleep(1000);
        LockSupport.unpark(t1); //发动

    }
}

/**
 * 使用wait notify实现
 *
 * 输出内容, 等待标记          下一个标记
 * a       1 条件满足       2
 * b       2               3
 * c       3               1
 */
class WaitNotify {
    // 当前的标记
    private int flag;
    // 循环次数
    private int loopNum;

    public WaitNotify(int flag, int loopNum) {
        this.flag = flag;
        this.loopNum = loopNum;
    }

    public void print(String str, int currentFlag, int nextFlag) {
        for (int i = 0; i < loopNum; i++) {
            synchronized (this) {
                while (currentFlag != flag) { // 当前的flag和传入的flag不相等,就等待
                    try {
                        this.wait(); // 进入waitset后,会释放锁,其他线程执行
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                // 相等
                System.out.print(str);
                this.flag = nextFlag;
                this.notifyAll();
            }
        }
    }
}

/**
 * 使用 ReentrantLock解决
 * 思路: 一开始让所有的线程都等待,然后发动一个,链式反应
 */
class AwaitSignal extends ReentrantLock {
    private int loopNumber;

    public AwaitSignal(int loopNumber) {
        this.loopNumber = loopNumber;
    }

    /**
     *  参数1:打印内容
     *  参数2:进入哪一间休息室等待
     *  参数3:下一间休息室
     *
     */
    public void print(String str, Condition current, Condition next){
        for (int i = 0; i < loopNumber; i++) {
            this.lock();
            try{
                try {
                    current.await(); // 让当前线程等待
                    // 被唤醒后打印
                    System.out.print(str);
                    next.signal();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }finally {
                this.unlock();
            }


        }
    }


}

/**
 *
 */
class ParkUnpark{
    private int loopNumber;

    public ParkUnpark(int loopNumber) {
        this.loopNumber = loopNumber;
    }

    /**
     *
     * @param str
     * @param next 唤醒下一个线程
     */
    public void print(String str,Thread next){
        for (int i = 0; i < loopNumber; i++) {
            LockSupport.park(); // 先暂停当前线程
            System.out.print(str);
            LockSupport.unpark(next); // 唤醒下一个线程
        }
    }

}