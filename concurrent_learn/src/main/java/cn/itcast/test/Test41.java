package cn.itcast.test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @description:
 * @author: malichun
 * @time: 2021/7/20/0020 10:34
 */
public class Test41 {
    public static void main(String[] args) {
        for (int i = 0; i < 5; i++) {
            demo(() -> new AtomicLong(0),
                (adder) -> adder.getAndIncrement() // 获取并自增
            );
        }

        // 使用原子累加器
        for (int i = 0; i < 5; i++) {
            // 使用原子累加器
            demo(LongAdder::new,
                LongAdder::increment
            );
        }

    }

    /**
     * @param adderSupplier () -> 结果  创建累加器对象
     * @param action        (参数) -> void    执行累加操作
     * @param <T>
     */
    private static <T> void demo(Supplier<T> adderSupplier, Consumer<T> action) {
        T adder = adderSupplier.get();
        List<Thread> ts = new ArrayList<>();
        // 4个线程,每人累加50万
        for (int i = 0; i < 4; i++) {
            ts.add(new Thread(() -> {
                for (int j = 0; j < 500000; j++) {
                    action.accept(adder);
                }
            }));
        }
        long start = System.nanoTime();
        ts.forEach(Thread::start);
        ts.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        long end = System.nanoTime();
        System.out.println(adder + " cost:" + (end - start) / 1000_000);


    }
}
