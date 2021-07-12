package cn.itcast.test;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 同步模式之保护性暂停
 * 增加超时效果
 */
@Slf4j(topic = "c.Test20")
public class Test20 {
    public static void main1(String[] args) {
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

    public static void sleep(long seconds){
        try {
            Thread.sleep(seconds*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //测试等待超时,没有超时
    public static void main(String[] args) {
        GuardedObjectV2 v2 = new GuardedObjectV2();
        new Thread(() -> {
            sleep(1);
            v2.complete(null);
            sleep(1);
            v2.complete(Arrays.asList("a","b","c"));
        }).start();


        Object response = v2.get(2500);
        if (response != null) {

            log.debug("get response:[{}] lines",response);
        }else{
            log.debug("can't get response");
        }

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

    // 获取结果的方法
    // timeout 表示最多要等多久 2000
    public Object get(long timeout){
        synchronized (this){
            // 没有结果
            // 记录一个开始时间
            long begin = System.currentTimeMillis();
            // 经历的时间
            long passedTime = 0;
            while(response == null){
                // 这一轮循环应该等待的时间
                long waitTime = timeout - passedTime;
                // 经历的时间超过了timeout的时间,最大等待时间,就退出循环
                if(waitTime <= 0){
                    break;
                }
                try {
                    this.wait(waitTime); //虚假唤醒,重新入这儿,不是还要等相同的时间
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // 循环结束的时候,做一个时间的差值,经历时间
                passedTime = System.currentTimeMillis() - begin;
            }

        }
        return response;
    }

    // 完成,结果已经产生
    public void complete(Object response){
        synchronized (this){
            //条件满足,等待线程通知
            this.response = response;
            log.debug("notify...");
            this.notifyAll();
        }
    }
}

