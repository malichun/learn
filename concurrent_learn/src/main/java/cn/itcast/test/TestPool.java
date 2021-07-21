package cn.itcast.test;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @description:
 * @author: malichun 自定义线程池
 * @time: 2021/7/21/0021 10:36
 */
@Slf4j(topic = "c.TestPool")
public class TestPool {
    public static void main(String[] args) {
        ThreadPool threadPool = new ThreadPool(2, 1000, TimeUnit.NANOSECONDS, 10);
        for (int i = 0; i < 5; i++) {
            int j = i;
            threadPool.execute(() -> {
                log.debug("{}", j);
            });
        }

    }
}

@Slf4j(topic = "c.ThreadPool")
class ThreadPool {
    // 任务队列
    private BlockingQueue<Runnable> taskQueue;

    // 线程集合
    private HashSet<Worker> workers = new HashSet<>();

    // 核心线程数
    private int coreSize;

    // 获取任务的超时时间
    private long timeout;

    private TimeUnit timeUnit;

    /**
     * 执行任务
     */
    public void execute(Runnable task) {
        // 当任务数没有包括核心coreSize时,直接交给worker对象执行
        //如果任务数 超过coresize , 加入任务队列,暂存起来
        synchronized (workers) {
            if (workers.size() < coreSize) {
                Worker worker = new Worker(task);
                log.debug("新增 worker{}, {}", worker, task);
                workers.add(worker);
                worker.start();
            } else {
                // 加入任务队列
                log.debug("加入任务队列{}", task);
                taskQueue.put(task);
            }
        }
    }


    /**
     * @param coreSize     核心线程数
     * @param timeout      超时时间
     * @param timeUnit     时间单位
     * @param queueCapcity 队列上线
     */
    public ThreadPool(int coreSize, long timeout, TimeUnit timeUnit, int queueCapcity) {
        this.coreSize = coreSize;
        this.timeout = timeout;
        this.timeUnit = timeUnit;
        this.taskQueue = new BlockingQueue<>(queueCapcity);
    }

    // 线程包装类
    class Worker extends Thread {
        private Runnable task;

        public Worker(Runnable task) {
            this.task = task;
        }

        @Override
        public void run() {
            // 执行任务
            // 1) 当task不为空,执行任务
            // 2) 当task执行完毕,再接着从任务队列获取任务并执行
//            while (task != null || (task = taskQueue.take()) != null) { // 没有超时时间的
            while (task != null || (task = taskQueue.poll(timeout,timeUnit)) != null) { // 带超时版本
                try {
                    log.debug("正在执行....{}", task);
                    task.run();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    task = null; // 任务执行完毕了
                }
            }
            synchronized (workers) {
                log.debug("worker被移除{}", this);
                workers.remove(this);
            }
        }
    }

}

/**
 * 阻塞队列
 *
 * @param <T>
 */
class BlockingQueue<T> {
    // 1.任务队列
    private Deque<T> queue = new ArrayDeque<>(); // 很多情况性能比LinkedList性能好

    // 2.锁
    private ReentrantLock lock = new ReentrantLock();

    //3.生产者条件变量
    private Condition fullWaitSet = lock.newCondition();

    //4.消费者条件变量
    private Condition emptyWaitSet = lock.newCondition();

    // 5.阻塞队列容量上限
    private int capcity;

    public BlockingQueue(int capcity) {
        this.capcity = capcity;
    }

    // 带超时的阻塞获取
    public T poll(long timeout, TimeUnit unit) {
        lock.lock(); // 获取锁
        try {
            // 将timeout统一转换为纳秒
            long nanos = unit.toNanos(timeout);
            while (queue.isEmpty()) { // 队列为空就等待
                try {
                    // 返回的是剩余的时间
                    if (nanos <= 0) { // 已经超时了
                        return null;
                    }
                    nanos = emptyWaitSet.awaitNanos(nanos);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // 队列不为空,有元素了
            // 获取队列头的元素,
            T t = queue.removeFirst(); // 队列头的元素
            // 唤醒等待空位的线程
            fullWaitSet.signal();
            return t;
        } finally {
            lock.unlock();
        }
    }

    // 阻塞获取
    public T take() {
        lock.lock(); // 获取锁
        try {
            while (queue.isEmpty()) { // 队列为空就等待
                try {
                    emptyWaitSet.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // 队列不为空,有元素了
            // 获取队列头的元素,
            T t = queue.removeFirst(); // 队列头的元素
            // 唤醒等待空位的线程
            fullWaitSet.signal();
            return t;
        } finally {
            lock.unlock();
        }
    }


    //阻塞添加
    public void put(T element) {
        lock.lock();
        try {
            // 看队列满不满,满的话阻塞
            while (queue.size() == capcity) {
                try {
                    fullWaitSet.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // 有空位,继续向下执行
            queue.addLast(element);
            // 唤醒阻塞的消费者线程
            emptyWaitSet.signal();


        } finally {
            lock.unlock();
        }
    }

    // 获取大小
    public int size() {
        lock.lock();
        try {
            return queue.size();
        } finally {
            lock.unlock();
        }
    }
}
