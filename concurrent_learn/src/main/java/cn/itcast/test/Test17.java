package cn.itcast.test;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by John.Ma on 2021/7/9 0009 21:59
 */
@Slf4j(topic = "c.Test17")
public class Test17 {
    public static void main(String[] args) throws InterruptedException {

        Room room = new Room();
        Thread t1 = new Thread(() ->{
            for(int j =0;j<5000;j++){
                room.increment();
            }
        });

        Thread t2 = new Thread(() ->{
            for(int j =0;j<5000;j++){
                room.decrement();
            }
        });

        t1.start();
        t2.start();

        t1.join(); // 当前线程等待两线程结束
        t2.join(); // 当前线程等待两线程结束
        log.debug("count: {}",room.get());

    }


    private static class Room{
        int value = 0;

        public void increment(){
            synchronized (this){
                value++;
            }
        }

        public void decrement(){
            synchronized (this){
                value--;
            }
        }

        public int get(){
            // 也需要加锁
            synchronized (this){
                return value;
            }
        }
    }

}

