package cn.itcast.n4;

import lombok.extern.slf4j.Slf4j;

/**
 * @description:
 * @author: malichun
 * @time: 2021/7/14/0014 13:10
 */
public class TestMultiLock {
    public static void main(String[] args) {
        BigRoom bigRoom = new BigRoom();
        new Thread(bigRoom::study,"小南").start();

        new Thread(bigRoom::sleep,"小女").start();
    }
}

@Slf4j(topic = "c.BigRoom")
class BigRoom{
    private final Object studyRoom = new Object();
    private final Object bedRoom = new Object();

    public void sleep(){
        synchronized (studyRoom){
            log.debug("sleep 2小时");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void study(){
        synchronized (bedRoom){
            log.debug("study 1 小时");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
