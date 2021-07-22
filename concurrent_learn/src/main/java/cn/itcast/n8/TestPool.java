package cn.itcast.n8;


import lombok.extern.slf4j.Slf4j;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by John.Ma on 2021/7/21 0021 19:54
 * 自定义线程池
 */
@Slf4j(topic = "c.TestPool")
public class TestPool {
    public static void main(String[] args) {
        ThreadPool threadPool = new ThreadPool(
            1,
            1000,
            TimeUnit.MILLISECONDS,
            1,
            (queue, task) -> { // 决绝策略
                // 1)死等
//                queue.put(task);
                // 2) 带超时的等待,超时后不添加了
//                 queue.offer(task,500,TimeUnit.MILLISECONDS);
                // 3) 放弃任务执行
                log.debug("放弃task{}",task);
                // 4) 让调用者抛出异常,不执行
//                throw new RuntimeException("任务执行失败 " + task); // 后面的代码不执行了
                // 5) 让调用者自己执行任务
                task.run(); // 主线程自己执行的
                //... 决策权下放
            });
        for (int i = 0; i < 4; i++) {
            int j = i;
            threadPool.execute(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.debug("{}", j);
            });
        }
    }
}

// 拒绝策略
@FunctionalInterface
interface RejectPolicy<T> {
    void reject(BlockingQueue<T> queue, T task);
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

    // 拒绝策略
    private RejectPolicy<Runnable> rejectPolicy;

    public ThreadPool(int coreSize, long timeout, TimeUnit timeUnit, int queueCapacity, RejectPolicy<Runnable> rejectPolicy) {
        this.coreSize = coreSize;
        this.timeout = timeout;
        this.timeUnit = timeUnit;
        this.taskQueue = new BlockingQueue<>(queueCapacity);
        this.rejectPolicy = rejectPolicy;// 拒绝策略
    }

    // 执行
    public void execute(Runnable task) {
        synchronized (workers) {
            // 当任务数没有超过coreSize时,直接交给worker对象执行
            // 如果任务数超过coreSize,加入队列暂存起来
            if (workers.size() < coreSize) {
                Worker worker = new Worker(task);
                log.debug("新增worker{}, {}", worker, task);
                workers.add(worker);
                worker.start(); // 启动线程执行
            } else { // 超过了,加入任务队列
//                taskQueue.put(task); // 队列满了死等
                // 1) 死等
                // 2) 带超时的等待
                // 3) 放弃任务执行
                // 4) 让调用者抛出异常,不执行
                // 5) 让调用者自己执行任务
                //... 决策权下方
                taskQueue.tryPut(rejectPolicy, task); // 拒绝策略

            }
        }

    }

    class Worker extends Thread {

        private Runnable task;

        public Worker(Runnable task) {
            this.task = task;
        }

        @Override
        public void run() {
            // 执行任务
            // 1) 当task不为空,执行任务
            // 2) 当task执行完成了, 后从任务队列中执行任务
//            while(task != null || (task = taskQueue.take()) != null){ // 任务队列await了,死等
            while (task != null || (task = taskQueue.poll(timeout, timeUnit)) != null) { // 任务队列await了,带超时时间
                try {
                    log.debug("正在执行...{}", task);
                    task.run();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    task = null; // 这个任务执行完毕了
                }
            }
            synchronized (workers) {
                log.debug("worker被移除 {}", this);
                workers.remove(this); // 当前对象从线程池中移除
            }
        }
    }
}

/**
 * 阻塞队列
 *
 * @param <T>
 */
@Slf4j(topic = "c.BlockingQueue")
class BlockingQueue<T> {
    // 1.任务队列
    private Deque<T> queue = new ArrayDeque<>();

    // 2.锁对象,保护队列头,对列尾的线程安全
    ReentrantLock lock = new ReentrantLock();

    // 3.生产者条件变量
    private Condition fullWaitSet = lock.newCondition();

    // 4.消费者条件变量,消费者为空的时候等待
    private Condition emptyWaitSet = lock.newCondition();

    // 5. 容量上限
    private int capacity;

    public BlockingQueue(int capacity) {
        this.capacity = capacity;
    }

    // 阻塞获取的方法,没有就一直等待
    public T take() {
        lock.lock();
        try {
            while (queue.isEmpty()) { //队列为空,等待
                try {
                    emptyWaitSet.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // 队列不为空
            // 获取队列头的元素
            T t = queue.removeFirst();
            fullWaitSet.signal();
            return t;
        } finally {
            lock.unlock();
        }
    }

    //阻塞获取的方法,加上超时时间
    public T poll(long timeout, TimeUnit unit) {
        // 将timeout 统一转换为纳秒
        long nanos = unit.toNanos(timeout);
        lock.lock();
        try {
            while (queue.isEmpty()) { //队列为空,等待
                if (nanos <= 0) {
                    return null;
                }
                try {
                    // 返回的是剩余的时间,防止虚假环境
                    nanos = emptyWaitSet.awaitNanos(nanos);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // 队列不为空
            // 获取队列头的元素
            T t = queue.removeFirst();
            fullWaitSet.signal();
            return t;
        } finally {
            lock.unlock();
        }
    }


    // 阻塞添加,如果队列满了一直死等
    public void put(T task) {
        lock.lock();
        try {
            while (queue.size() == capacity) {
                try {
                    log.debug("等待加入任务队列 {}", task);
                    fullWaitSet.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            log.debug("加入任务队列 {}", task);
            queue.addLast(task);
            emptyWaitSet.signal(); // 唤醒线程去消费
        } finally {
            lock.unlock();
        }
    }

    // 带超时时间的阻塞添加,返回true代表成功
    public boolean offer(T task, long timeout, TimeUnit timeUnit) {
        lock.lock();
        try {
            long nanos = timeUnit.toNanos(timeout);
            while (queue.size() == capacity) {
                try {
                    log.debug("等待加入任务队列 {}", task);
                    if (nanos <= 0) { //超时,添加失败了
                        return false;
                    }
                    nanos = fullWaitSet.awaitNanos(nanos);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            log.debug("加入任务队列 {}", task);
            queue.addLast(task);
            emptyWaitSet.signal(); // 唤醒线程去消费
            return true; // 添加成功
        } finally {
            lock.unlock();
        }
    }

    // 获取队列大小
    public int size() {
        lock.lock();
        try {
            return queue.size();
        } finally {
            lock.unlock();
        }
    }


    public void tryPut(RejectPolicy<T> rejectPolicy, T task) {
        lock.lock();
        try {
            // 判断队列是否已满
            if (queue.size() == capacity) {
                rejectPolicy.reject(this, task); // 权利下放
            } else { // 队列有空闲
                log.debug("加入任务队列{}", task);
                queue.addLast(task);
                emptyWaitSet.signal();
            }
        } finally {
            lock.unlock();
        }
    }
}
