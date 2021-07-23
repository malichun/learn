package cn.itcast.n8;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by John.Ma on 2021/7/23 0023 5:34
 * 线程池饥饿
 */
@Slf4j(topic = "c.TestDeadLock")
public class TestDeadLock {
    static final List<String> MENU = Arrays.asList("地三鲜", "宫保鸡丁", "辣子鸡丁", "烤鸡翅");
    static Random RANDOM = new Random();

    static String cooking() {
        return MENU.get(RANDOM.nextInt(MENU.size()));
    }

    public static void main(String[] args) {
        ExecutorService waiterPool = Executors.newFixedThreadPool(2);
        ExecutorService cookPool = Executors.newFixedThreadPool(2);
        waiterPool.execute(() -> {
            log.debug("处理点餐");
            Future<String> f = cookPool.submit(() -> {
                log.debug("做菜");
                return cooking();
            });
            try {
                log.debug("上菜:{}",f.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
        waiterPool.execute(() -> {
            log.debug("处理点餐");
            Future<String> f = cookPool.submit(() -> {
                log.debug("做菜");
                return cooking();
            });
            try {
                log.debug("上菜:{}",f.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
        waiterPool.execute(() -> {
            log.debug("处理点餐");
            Future<String> f = cookPool.submit(() -> {
                log.debug("做菜");
                return cooking();
            });
            try {
                log.debug("上菜:{}",f.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });


    }

    /**
     * 饥饿的案例
     * @param args
     */
    public static void deadLockDemo(String[] args) {
        ExecutorService pool = Executors.newFixedThreadPool(2);

        pool.execute(() -> {
            log.debug("处理点餐...");
            Future<String> f = pool.submit(() -> {
                log.debug("做菜");
                return cooking();
            });
            try {
                log.debug("上菜:{}", f.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
        pool.execute(() -> {
            log.debug("处理点餐...");
            Future<String> f = pool.submit(() -> {
                log.debug("做菜");
                return cooking();
            });
            try {
                log.debug("上菜:{}", f.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
    }

}
