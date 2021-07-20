package cn.itcast.test;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * @description:
 * @author: malichun
 * @time: 2021/7/20/0020 9:21
 */
@Slf4j(topic = "c.Test36")
public class Test36 {
    static AtomicStampedReference<String> ref = new AtomicStampedReference<>("A",0);

    public static void main(String[] args) throws InterruptedException {
        log.debug("main start ...");
        // 获取A值
        String prev = ref.getReference();
        int stamp = ref.getStamp();
        log.debug("{}",stamp);
        other();
        Thread.sleep(1000);
        int stamp1 = ref.getStamp();
        log.debug("{}",stamp1);
        log.debug("change A->C {}",ref.compareAndSet(prev,"C",stamp,stamp + 1));

    }

    private static void other(){
        new Thread(() ->{
            int stamp = ref.getStamp();
            log.debug("{}",stamp);
            log.debug("change A->B {}",ref.compareAndSet(ref.getReference(),"B",stamp,stamp+1));
        },"t1").start();
        new Thread(() ->{
            int stamp = ref.getStamp();
            log.debug("{}",stamp);
            log.debug("change B->A {}",ref.compareAndSet(ref.getReference(),"A",stamp,stamp+1));
        },"t2").start();

    }
}
