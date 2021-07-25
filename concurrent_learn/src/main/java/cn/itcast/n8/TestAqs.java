package cn.itcast.n8;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @description:
 * @author: malichun
 * @time: 2021/7/23/0023 18:35
 *
 */
@Slf4j(topic = "c.TestAqs")
public class TestAqs {

    public static void main(String[] args) {
        MyLock lock = new MyLock();
        new Thread(() ->{
            lock.lock();
//            lock.lock(); // 不可重入
            try{
                log.debug("locking...");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }finally {
                log.debug("unlocking ...");
                lock.unlock();
            }
        },"t1").start();

//        new Thread(() -> {
//            lock.lock();
//            try {
//                log.debug("locking...");
//            } finally {
//                log.debug("unlocking...");
//                lock.unlock();
//            }
//        },"t2").start();
    }
}

//自定义锁,不可重入
class MyLock implements Lock{

    //同步工具
    // 独占锁
    class MySync extends AbstractQueuedSynchronizer{
        // 尝试获取锁
        @Override
        protected boolean tryAcquire(int arg) { // arg现在用不着,不可重入锁
            // 状态,从0 变为 1, 表示获取锁
            if (compareAndSetState(0, 1)) {
                // 加上锁,并设置owner为当前
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }else{
                return false; //表示加锁失败
            }
        }

        // 尝试释放锁
        @Override
        protected boolean tryRelease(int arg) {
            setExclusiveOwnerThread(null);
            setState(0); // 表示解锁 volatile可以保证重排序 后面加写屏障
            return true;
        }

        //是否持有独占锁
        @Override
        protected boolean isHeldExclusively() {
            return getState() == 1;
        }

        public Condition newCondition(){
            return new ConditionObject();
        }
    }

    private MySync sync = new MySync();

    @Override
    public void lock() { // 加锁(不成功会进入等待队列)
        sync.acquire(1);
    }

    @Override // 加锁,可打断
    public void lockInterruptibly() throws InterruptedException {
        sync.acquireInterruptibly(1);
    }

    @Override // 尝试加锁(1次)
    public boolean tryLock() {
        return sync.tryAcquire(1);
    }

    @Override // 尝试加锁,带超时
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return sync.tryAcquireNanos(1, unit.toNanos(time));
    }

    @Override // 解锁
    public void unlock() {
        sync.release(1); // 顺带唤醒等待线程
    }

    @Override // 创建条件变量
    public Condition newCondition() {
        return sync.newCondition();
    }
}

