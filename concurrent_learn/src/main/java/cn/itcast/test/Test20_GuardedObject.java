package cn.itcast.test;

import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * 同步模式之保护性暂停
 * 增加超时效果
 */
@Slf4j(topic = "c.Test20")
public class Test20_GuardedObject {
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

    public static void main2(String[] args) {
        // 测试超时版本,没有超时
        GuardedObjectV2 v2 = new GuardedObjectV2();
        // 产生结果
        new Thread(() -> {
            log.debug("begin");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            v2.complete(new Object());

        },"t1").start();

        // 获取结果
        new Thread(() -> {
            log.debug("begin");
            Object response = v2.get(2500);
            log.warn("结果是: {}", response);
        },"t2").start();




    }

    // 测试邮箱送信
    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 3; i++) {
            new People().start(); // 等着收信
        }

        Thread.sleep(1000);

        // 获取信件的编号
        MailBoxes.getIds().forEach(id -> {
            new PostMan(id,"内容"+id).start();
        });
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

@Slf4j(topic = "c.GuardedObjectV3")
class GuardedObjectV3 {
    //标识Guarded Object
    private int id;

    public GuardedObjectV3(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    // 结果
    private Object response;

    public Object get(long millis){
        synchronized (this){
            // 1)记录最初时间
            long begin = System.currentTimeMillis();
            // 2) 已经经历的时间
            long timePassed = 0;
            while(response == null){
                // 4 假设millis 是1000,结果在400时唤醒了,那么还有600要等
                long waitTime = millis - timePassed;
//                log.debug("waitTime: {}", waitTime);
                if(waitTime <= 0){
//                    log.debug("break...");
                    break;
                }
                try{
                    this.wait(waitTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // 3) 如果提前被唤醒,这时已经经历的时间建设为400
                timePassed = System.currentTimeMillis() - begin;
//                log.debug("timePassed:{} , object is null {}",timePassed,response == null);
            }
        }
        return response;
    }

    public void complete(Object response){
        synchronized (this){
            //条件满足,等待线程通知
            this.response = response;
//            log.debug("notify...");
            this.notifyAll();
        }
    }

}

// 邮箱类,与业务无关,可以重用
class MailBoxes{
    private static Map<Integer, GuardedObjectV3> boxes = new Hashtable<>();

    private static int id =1;

    public static synchronized int generateId(){
        return id++;
    }

    // 根据id获取对象
    public static GuardedObjectV3 getGuardedObject(int id){
        return boxes.remove(id);
    }

    public static GuardedObjectV3 createGuardedObject(){
        GuardedObjectV3 go = new GuardedObjectV3(generateId());
        boxes.put(go.getId(),go);
        return go;
    }

    public static Set<Integer> getIds() {
        return boxes.keySet();
    }

}

// 居民
@Slf4j(topic = "c.People")
class People extends Thread{
    @Override
    public void run() {
        // 收信
        GuardedObjectV3 guardedObject = MailBoxes.createGuardedObject();
        log.debug("开始收信 id:{}",guardedObject.getId());
        Object mail = guardedObject.get(5000);
        log.debug("收到信 id:{},内容:{}",guardedObject.getId(), mail);

    }
}

// 邮递员
@Slf4j(topic = "c.PostMan")
class PostMan extends Thread{
    private int id; // 信箱id
    private String mail; // 信件内容

    public PostMan(int id,String mail){
        this.id = id;
        this.mail = mail;
    }

    @Override
    public void run() {
        GuardedObjectV3 guardedObject = MailBoxes.getGuardedObject(id);
        // 产生邮件内容
        log.debug("送信 id:{}, 内容:{}",guardedObject.getId(), mail);
        guardedObject.complete(mail);
    }
}

