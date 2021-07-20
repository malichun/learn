package cn.itcast.test;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * Created by John.Ma on 2021/7/19 0019 22:23
 */
@Slf4j(topic = "c.Test36")
public class Test36 {
//
//    static AtomicReference<String> ref = new AtomicReference<>("A");
//
//    public static void main(String[] args) throws InterruptedException {
//        log.debug("main start...");
//        // 获取值
//        String prev = ref.get();
//        other();
//        Thread.sleep(1000);
//
//        log.debug("change A->C {}",ref.compareAndSet(prev,"C"));
//
//    }
//
//    private static void other() throws InterruptedException {
//        new Thread(() -> {
//            log.debug("change A->B {}", ref.compareAndSet(ref.get(), "B"));
//        }, "t1").start();
//        Thread.sleep(500);
//        new Thread(() -> {
//            log.debug("change B->A {}", ref.compareAndSet(ref.get(), "A"));
//        }, "t2").start();
//    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // 使用AtomicStampedReference, 谁改,谁版本+1
    static AtomicStampedReference<String> ref = new AtomicStampedReference<>("A",0);

    public static void main(String[] args) throws InterruptedException {
        log.debug("main start ....");
        String prev = ref.getReference();
        Thread.sleep(1000);

        log.debug("change A->C {}",ref.compareAndSet(prev,"C",ref.getStamp(),ref.getStamp() + 1));
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
