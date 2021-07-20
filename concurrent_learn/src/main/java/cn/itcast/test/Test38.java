package cn.itcast.test;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicMarkableReference;

/**
 * @description:
 * @author: malichun
 * @time: 2021/7/20/0020 9:35
 */
@Slf4j(topic = "c.Test38")
public class Test38 {
    public static void main(String[] args) throws InterruptedException {
        GarbageBag bag = new GarbageBag("装满了垃圾");
        //参数2 mark可以看做一个标记,表示垃圾袋满了
        AtomicMarkableReference<GarbageBag> ref = new AtomicMarkableReference<>(bag,true);

        log.debug("主线程 start....");
        GarbageBag prev = ref.getReference();
        log.debug(prev.toString());

        new Thread(() -> {
            log.debug("打扫卫生线程 start...");
            bag.setDesc("空垃圾袋");
            while(!ref.compareAndSet(bag,bag,true,false)){}
            log.debug(bag.toString());
        },"保洁阿姨").start();

        Thread.sleep(1000);
        log.debug("主线程想换一只新的垃圾袋?");
        boolean success = ref.compareAndSet(prev,new GarbageBag("空垃圾袋"),true,false);
        log.debug("换了么?"+ success);
        log.debug(ref.getReference().toString());

    }
}

/**
 * 垃圾袋
 */
class GarbageBag{
    String desc;

    public GarbageBag(String desc) {
        this.desc = desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return super.toString() + " " + desc;
    }
}
