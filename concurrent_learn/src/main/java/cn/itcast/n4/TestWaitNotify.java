package cn.itcast.n4;

import lombok.extern.slf4j.Slf4j;

/**
 * @description:
 * @author: malichun
 * @time: 2021/7/12/0012 17:30
 */
@Slf4j(topic = "c.TestWaitNotify")
public class TestWaitNotify {
    final static Object obj = new Object();

    public static void main(String[] args) throws InterruptedException {
        new Thread(() -> {
            synchronized (obj) {
                log.debug("执行.....");
                try {
//                    obj.wait(); // 让线程在obj上一直等待下去,让出执行权
                    obj.wait(1000); // 进入waitset 1秒钟(会释放锁),之后会唤醒,继续外面抢锁对象
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.debug("其他代码....");
            }
        },"t1").start();

//        new Thread(() -> {
//            synchronized (obj){
//                log.debug("执行.....");
//                try {
//                    obj.wait(); // 让线程在obj上一直等待下去
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                log.debug("其他代码....");
//            }
//        },"t2").start();
//
//        // 主线程2秒后执行
//        Thread.sleep(2000);
//        log.debug("唤醒obj上面的其他线程");
//        synchronized (obj){
////            obj.notify(); // 唤醒obj上的一个线程
//            obj.notifyAll(); // 唤醒obj上的所有线程
//        }




    }
}
