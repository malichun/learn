package cn.itcast.test;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by John.Ma on 2021/7/8 0008 1:40
 */
public class Test13 {

    public static void main(String[] args) {
        TwoPhaseTermination tpt = new TwoPhaseTermination();
        tpt.start();
        try {
            Thread.sleep(3500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        tpt.stop();
    }
}

@Slf4j(topic = "c.TwoPhaseTermination")
class TwoPhaseTermination{
    private Thread monitor;

    // 启动监控线程
    public void start(){
        monitor = new Thread(() -> {
            while(true){
                Thread current = Thread.currentThread();
                if(current.isInterrupted()){
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
    public void stop(){
        monitor.interrupt(); // 通知打断
    }
}
