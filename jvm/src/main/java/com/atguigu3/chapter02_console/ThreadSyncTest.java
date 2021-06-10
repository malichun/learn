package com.atguigu3.chapter02_console;

/**
 * @description:
 * @author: malichun
 * @time: 2021/6/10/0010 17:18
 */
public class ThreadSyncTest {
    public static void main(String[] args) {
        Number number = new Number();
        Thread t1 = new Thread(number);
        Thread t2 = new Thread(number);

        t1.setName("线程1");
        t2.setName("线程2");

        t1.start();
        t2.start();
    }

}

class Number implements Runnable{
    private int number = 1;

    @Override
    public void run() {
        while(true){
            synchronized (this){
                if(number <= 300){
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(Thread.currentThread().getName()+":"+number);
                    number ++;
                }else{
                    break;
                }
            }
        }
    }
}
