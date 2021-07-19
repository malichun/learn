package cn.itcast.test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntUnaryOperator;

/**
 * @description:
 * @author: malichun
 * @time: 2021/7/19/0019 12:40
 */
public class Test34 {
    public static void main(String[] args) {

//        AtomicInteger i = new AtomicInteger(0);
//        System.out.println(i.incrementAndGet()); // ++i 1
//        System.out.println(i.getAndIncrement()); // i++ 1
//        System.out.println(i.get()); // 2
//
////        i.decrementAndGet();
////        i.getAndDecrement();
//
//        System.out.println(i.getAndAdd(5)); // 2    7
//        System.out.println(i.addAndGet(5)); // 12   12

//        System.out.println(i.get());



        // 复杂运算
        AtomicInteger i = new AtomicInteger(5);

//       updateAndGet(i,value -> value * 10);

        i.updateAndGet(value -> value * 10);
        System.out.println(i.get());


    }

    // 自己的updateAndGet方法
    public static void updateAndGet(AtomicInteger i, IntUnaryOperator operator){
        while(true){
            int prev = i.get();
            int next = operator.applyAsInt(prev);
            if(i.compareAndSet(prev,next)){
                break;
            }
        }
    }
}
