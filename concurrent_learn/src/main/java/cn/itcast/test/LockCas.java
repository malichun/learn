package cn.itcast.test;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @description:
 * @author: malichun
 * @time: 2021/7/20/0020 11:14
 */
@Slf4j(topic = "c.Test42")
public class LockCas {
    // 0没加锁
    // 1加锁
    private AtomicInteger state = new AtomicInteger(0);

    public void lock(){
        while(true){
            if(state.compareAndSet(0,1)){ // 从无锁状态变为有所状态
                break;
            }
        }
    }

    public void unlock(){
        log.debug("unlock...");
        state.set(0);
    }

    public static void main(String[] args) {
        LockCas lock = new LockCas();
        new Thread(() ->{
            log.debug("begin...");
            lock.lock();

            try {
                log.debug("lock...");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                lock.unlock();
            }
        }).start();

        new Thread(() ->{
            log.debug("begin...");
            lock.lock();

            try {
                log.debug("lock...");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                lock.unlock();
            }
        }).start();


    }
}
