package cn.itcast.n8;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by John.Ma on 2021/7/23 0023 7:03
 * 如何让每周四 18:00:00 定时执行任务？
 */
public class TestScheduler {
    public static void main(String[] args) {
        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();

        // 获取本周四18:00:00.000
        LocalDateTime thursday = now.with(DayOfWeek.THURSDAY).withHour(18).withMinute(0)
            .withSecond(0).withNano(0);
        // 如果当前时间已经超过 本周四18:00:00.000 ,那么找下周四18:00:00.000
        if(now.compareTo(thursday) >= 0){
            thursday = thursday.plusWeeks(1);
        }
        // 计算时间差,即延时执行时间
        long initialDelay = Duration.between(now,thursday).toMillis();
        // 计算时间间隔,一周的毫秒数
        long oneWeek = 7 * 24 * 3600 * 1000;
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
        System.out.println("开始时间:"+ new Date());
        executor.scheduleAtFixedRate(() -> {
            System.out.println("执行时间:"+ new Date());

        },initialDelay,oneWeek, TimeUnit.MINUTES);

    }

}
