package cn.itcast.test;

import lombok.extern.slf4j.Slf4j;
import org.openjdk.jol.info.ClassLayout;

/**
 * @description:
 * @author: malichun
 * @time: 2021/7/12/0012 10:22
 */
// 偏向锁 -> 轻量锁, 两个线程执行需要错开
@Slf4j(topic = "c.TestBiased")
public class TestBiased {
    public static void main(String[] args) throws InterruptedException {
        Dog dog = new Dog();
        new Thread(() ->{
            log.warn(ClassLayout.parseInstance(dog).toPrintable());

            synchronized (dog){
                log.warn(ClassLayout.parseInstance(dog).toPrintable());
            }

            log.warn(ClassLayout.parseInstance(dog).toPrintable());

            synchronized (TestBiased.class) {
                TestBiased.class.notify();
            }

        },"t1").start();

        new Thread(() ->{
            // 要错开
            synchronized (TestBiased.class){
                try {
                    TestBiased.class.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


            log.warn(ClassLayout.parseInstance(dog).toPrintable());

            synchronized (dog){
                log.warn(ClassLayout.parseInstance(dog).toPrintable());
            }

            log.warn(ClassLayout.parseInstance(dog).toPrintable());



        },"t2").start();





    }


}

class Dog{}


