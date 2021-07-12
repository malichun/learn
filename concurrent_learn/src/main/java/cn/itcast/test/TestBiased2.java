package cn.itcast.test;

import lombok.extern.slf4j.Slf4j;
import org.openjdk.jol.info.ClassLayout;

import java.util.Vector;
import java.util.concurrent.locks.LockSupport;

/**
 * @description:
 * @author: malichun
 * @time: 2021/7/12/0012 11:28
 */

@Slf4j(topic = "c.TestBiased2")
public class TestBiased2 {

    /**
     * // 批量重偏向
     * @throws InterruptedException
     */
    private static void test3() throws InterruptedException {
        Vector<Dog> list = new Vector<>();
        Thread t1 = new Thread(() -> {
            for(int i=0;i<30;i++){
                Dog d = new Dog();
                list.add(d);
                synchronized (d){ // 全部是当前线程的id,偏向锁
                    log.debug(i + "\t"+ ClassLayout.parseInstance(d).toPrintable());
                }
            }

            synchronized (list){
                list.notify();
            }
        }, "t1");
        t1.start();


        Thread t2 = new Thread(() -> {
            synchronized (list){
                try {
                    list.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            log.debug("============================================================================");
            for (int i = 0; i < 30; i++) {
                Dog d = list.get(i);
                log.debug(i + "\t" + ClassLayout.parseInstance(d).toPrintable());
                synchronized (d){ // 撤销t1的偏向锁,超过的次数多了(阈值),会重偏向
                    log.debug(i + "\t" + ClassLayout.parseInstance(d).toPrintable());
                }
                log.debug(i+"\t"+ClassLayout.parseInstance(d).toPrintable());
            }
        });
        t2.start();
    }

    static Thread t1,t2,t3;
    /**
     * 批量撤销
     * @throws InterruptedException
     */
    private  static void test4() throws InterruptedException{
        Vector<Dog> list = new Vector<>();

        int loopNumber = 39;
        t1 = new Thread(()->{
            for(int i=0;i<loopNumber;i++){
                Dog d = new Dog();
                list.add(d);
                synchronized (d){
                    log.debug(i + "\t" + ClassLayout.parseInstance(d).toPrintable());
                }
            }
            LockSupport.unpark(t2); // 唤醒t2
        },"t1");
        t1.start();

        t2 = new Thread(() ->{
            LockSupport.park();
            log.debug("=================>");
            for (int i = 0; i < loopNumber; i++) {
                Dog d = list.get(i);
                log.debug(i + "\t"+ClassLayout.parseInstance(d).toPrintable());
                synchronized (d){
                    log.debug(i + "\t"+ClassLayout.parseInstance(d).toPrintable());
                }
                log.debug(i + "\t"+ClassLayout.parseInstance(d).toPrintable());
            }
            LockSupport.unpark(t3);
        },"t2");
        t2.start();

        t3 = new Thread(() ->{
            LockSupport.park();
            log.debug("================>");
            for (int i = 0; i < loopNumber; i++) {
                Dog d = list.get(i);
                log.debug(i + "\t"+ClassLayout.parseInstance(d).toPrintable());
                synchronized (d){
                    log.debug(i + "\t"+ClassLayout.parseInstance(d).toPrintable());
                }
                log.debug(i + "\t"+ClassLayout.parseInstance(d).toPrintable());
            }
        },"t3");
        t3.start();

        t3.join();
        log.debug(ClassLayout.parseInstance(new Dog()).toPrintable());
    }

    public static void main(String[] args) throws InterruptedException {
//        test3(); // 批量重偏向
        test4(); // 批量撤销
    }
}
