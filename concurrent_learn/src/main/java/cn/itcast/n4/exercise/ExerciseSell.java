package cn.itcast.n4.exercise;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

/**
 * Created by John.Ma on 2021/7/11 0011 9:46
 */
@Slf4j(topic = "c.ExerciseTransfer")
public class ExerciseSell {
    public static void main(String[] args) {
        //模拟多人卖票
        TicketWindow ticketWindow = new TicketWindow(2000);
        List<Thread> list = new ArrayList<>();
        //用来存储买出多少张票
        List<Integer> sellCount = new Vector<>();
        for(int i = 0;i<2000;i++){
            Thread t = new Thread(() -> {
                // 分析这里的竞态条件
                int count = ticketWindow.sell(randomAmount());
                sellCount.add(count);
            });
            list.add(t);
            t.start();
        }

        list.forEach((t) -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        // 卖出去的票求和
        log.debug("selled count: {}", sellCount.stream().mapToInt(c -> c).sum());
        // 剩余票数
        log.debug("remainder count: {}",ticketWindow.getCount());
    }

    // Random为线程安全
    static Random random = new Random();

    public static int randomAmount(){
        return random.nextInt(5)+1;
    }

}

// 售票窗口
class TicketWindow {
    private int count;

    public TicketWindow(int count) {
        this.count = count;
    }

    // 获取余票数量
    public int getCount(){
        return count;
    }

    // 售票
    public int sell(int amount){
        if(this.count >= amount){
            this.count-= amount;
            return amount;
        }else {
            return 0;
        }
    }
}
