package cn.itcast.n4;

import lombok.extern.slf4j.Slf4j;

/**
 * @description:
 * @author: malichun
 * @time: 2021/7/12/0012 18:03
 */
// notifyAll
@Slf4j(topic = "c.TestCorrectPostureStep5")
public class TestCorrectPostureStep5 {
    static final Object room = new Object();
    static boolean hasCigarette = false; // 有没有烟
    static boolean hasTakeout = false;

    public static void main(String[] args) throws InterruptedException {

        new Thread(() -> {
            synchronized (room){
                log.debug("有烟没[{}]", hasCigarette);
                try {
                    room.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.debug("有烟没?[{}]",hasCigarette);
                while(!hasCigarette) {
                    log.debug("没烟,先歇会!");
                    try{
                        room.wait();
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
                log.debug("可以开始干活了!");
            }
        },"小南").start();

        new Thread(() -> {
            synchronized (room){
                Thread thread = Thread.currentThread();
                log.debug("外卖送到没?[{}]",hasTakeout);
                if(!hasTakeout){
                    log.debug("没外卖,先歇会!");
                    try {
                        room.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.debug("外卖送到没?[{}]",hasTakeout);
                if(hasTakeout){
                    log.debug("可以开始干活了");
                }else{
                    log.debug("没干成活");
                }
            }
        },"小女").start();

        Thread.sleep(1000);
        new Thread(() -> {
            synchronized (room){
                hasTakeout = true;
                log.debug("外卖送到了噢!");
                room.notifyAll();
            }
        },"送外卖的").start();
    }

}
