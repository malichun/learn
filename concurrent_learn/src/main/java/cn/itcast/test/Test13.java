package cn.itcast.test;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by John.Ma on 2021/7/8 0008 1:40
 */
public class Test13 {
    // 使用打断方式两阶段终止
    public static void main1(String[] args) {
        TwoPhaseTermination tpt = new TwoPhaseTermination();
        tpt.start();
        try {
            Thread.sleep(3500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        tpt.stop();
    }

    public static void main(String[] args) {
        TwoPhaseTermination2 tpt = new TwoPhaseTermination2();
        tpt.start(); // 会启动一个新的线程
        tpt.start();

        try {
            Thread.sleep(3500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        tpt.stop(); // 主线程修改stop=true
    }
}

/**
 * 使用线程的打断方法
 */
@Slf4j(topic = "c.TwoPhaseTermination")
class TwoPhaseTermination {
    private Thread monitor;

    // 启动监控线程
    public void start() {
        monitor = new Thread(() -> {
            while (true) {
                Thread current = Thread.currentThread();
                if (current.isInterrupted()) {
                    log.debug("料理后事");
                    break;
                }
                try {
                    Thread.sleep(1000);//情况1 都有可能被打断
                    log.debug("执行监控记录"); // 情况2
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    current.interrupt();// 重新设置打断标记(sleep 会清除打断标记)
                }
            }
        });

        monitor.start();
    }

    // 停止监控线程
    public void stop() {
        monitor.interrupt(); // 通知打断
    }
}


/**
 * 改进,使用volatile关键字
 */
@Slf4j(topic = "c.TwoPhaseTermination2")
class TwoPhaseTermination2 {
    // 监控线程
    private Thread monitorThread;

    // 控制线程是继续运行还是退出
    private volatile boolean stop = false;
    // 判断是否执行过start方法
    private boolean starting = false;

    // 启动监控线程
    public void start() {
        synchronized(this){
            if(starting){  // 犹豫模式,保证原子性
                return ;
            }
            starting = true;
            monitorThread = new Thread(() -> {
                while (true) {
                    Thread current = Thread.currentThread();
                    // 是否被打断
                    if (stop) { // 在monitor线程看stop
                        log.debug("料理后事..");
                        break;
                    }
                    try {
                        Thread.sleep(1000);
                        log.debug("执行监控记录");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }, "monitor");
            monitorThread.start(); // 执行线程
        }

    }

    public void stop(){
        stop = true;
        monitorThread.interrupt(); // 打断sleep,不需要打断标记了
    }



}
