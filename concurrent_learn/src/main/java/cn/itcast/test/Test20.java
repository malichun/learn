package cn.itcast.test;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * 同步模式之保护性暂停
 * 增加超时效果
 */
@Slf4j(topic = "c.Test20")
public class Test20 {
    public static void main(String[] args) {
        // 线程1等待线程2下载结果
        GuardedObject guardedObject = new GuardedObject();
        new Thread(() -> {
            try{
                // 子线程执行下载
                List<String> response = new ArrayList<String>(){{
                    add("123");
                    add("234");
                }};
                Thread.sleep(3000);
                log.debug("处理完成......");
                guardedObject.complete(response);

            }catch (Exception e){
                e.printStackTrace();
            }
        }).start();

        log.debug("waiting....");
        // 主线程阻塞等待
        Object response = guardedObject.get();
        log.debug("get response:[{}]",response);

    }
}


// 作为一个桥梁
class GuardedObject{
    // 结果
    private Object response;

    // 获取结果的方法
    public Object get(){
        synchronized (this){
            while(response == null){ // 还没有结果
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return response;
        }
    }

    // 完成,结果已经产生
    public void complete(Object response){
        synchronized (this){
            // 给结果成员变量赋值
            this.response = response;
            this.notifyAll();
        }
    }

}

/**
 * 增加带超时版本的
 */
@Slf4j(topic = "c.GuardedObjectV2")
class GuardedObjectV2{
    // 结果
    private Object response;
    private final Object lock = new Object();

    public Object get(long millis){
        synchronized (lock){
            // 1)记录最初时间
            long begin = System.currentTimeMillis();
            // 2) 已经经历的时间
            long timePassed = 0;
            while(response == null){
                // 4 假设millis 是1000,结果在400时唤醒了,那么还有600要等
                long waitTime = millis - timePassed;
                log.debug("waitTime: {}", waitTime);
                if(waitTime <= 0){
                    log.debug("break...");
                    break;
                }
                try{
                    lock.wait(waitTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // 3) 如果提前被唤醒,这时已经经历的时间建设为400
                timePassed = System.currentTimeMillis() - begin;
                log.debug("timePassed:{} , object is null {}",timePassed,response == null);
            }
        }
        return response;
    }

    public void complete(Object response){
        synchronized (lock){
            //条件满足,等待线程通知
            this.response = response;
            log.debug("notify...");
            lock.notifyAll();
        }
    }
}

