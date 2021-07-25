package cn.itcast.n8;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.StampedLock;

/**
 * Created by John.Ma on 2021/7/24 0024 12:54
 */
public class TestReadWriteLock {
    public static void main(String[] args) {
//        DataContainer dataContainer = new DataContainer();
//        new Thread(() -> {
//            dataContainer.read();
//        }, "t1").start();
//        new Thread(() -> {
//            dataContainer.write();
//        }, "t2").start();

        DataContainerStamped dataContainerStamped = new DataContainerStamped(1);
        new Thread(() -> {
            dataContainerStamped.read(1);
        }, "t1").start();
        new Thread(() -> {
            dataContainerStamped.read(0);
        }, "t2").start();


    }

}

@Slf4j(topic = "c.DataContainer")
class DataContainer {
    private Object data; // 需要保护的共享数据
    private ReentrantReadWriteLock rw = new ReentrantReadWriteLock();

    // 读锁
    private ReentrantReadWriteLock.ReadLock r = rw.readLock();
    // 写锁
    private ReentrantReadWriteLock.WriteLock w = rw.writeLock();

    public Object read() {
        log.debug("获取读锁...");
        r.lock();
        try {
            // 读取操作
            log.debug("读取");
            sleep(1);
            return data;
        } finally {
            log.debug("释放读锁...");
            r.unlock();
        }
    }

    // 写入操作
    public void write() {
        log.debug("获取写锁...");
        w.lock();
        try {
            log.debug("写入");
            sleep(1);
        } finally {
            log.debug("释放写锁...");
            w.unlock();
        }
    }

    private void sleep(int seconds){
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}


/**
 * stampedLock
 */
@Slf4j(topic = "c.DataContainerStamped")
class DataContainerStamped{
    private int data;
    private final StampedLock lock = new StampedLock();

    public DataContainerStamped(int data) {
        this.data = data;
    }

    public int read(int readTime){
        long stamp = lock.tryOptimisticRead();
        log.debug("optimistic read locking...{}",stamp);
        sleep(readTime);
        if(lock.validate(stamp)){ // 如果戳没有变
            log.debug("read finish... {}",stamp);
            return data;
        }
        // 锁升级 - 读锁
        log.debug("updating to read lock... {}",stamp);
        try{
            stamp = lock.readLock();
            log.debug("read lock {}",stamp);
            sleep(readTime);
            log.debug("read finish ... {}, data:{}",stamp,data);
            return data;
        }finally {
            lock.unlockRead(stamp);
        }

    }

    public void write(int newData){
        long stamp = lock.writeLock();
        log.debug("write lock {}",stamp);
        try{
            sleep(2);
            this.data = newData;
        }finally {
            lock.unlockWrite(stamp);
        }
    }

    private static void sleep(int seconds){
        try {
            Thread.sleep(1000 * seconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
