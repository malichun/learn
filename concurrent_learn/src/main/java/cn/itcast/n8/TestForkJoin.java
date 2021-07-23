package cn.itcast.n8;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * @description:
 * @author: malichun
 * @time: 2021/7/23/0023 13:24
 */
@Slf4j(topic = "c.TestForkJoin")
public class TestForkJoin {
    public static void main(String[] args) {
        ForkJoinPool pool = new ForkJoinPool(4);
//        System.out.println(pool.invoke(new MyTask(5)));
        System.out.println(pool.invoke(new AddTask3(0, 5)));

        // 任务拆分  5 + new MyTask(4) | 4 + new MyTask(3) | 3 + new MyTask(2) | 2 + new MyTask(1)
    }
}

// 创建任务对象
// 1~n 数字整数的和
@Slf4j(topic = "c.MyTask")
class MyTask extends RecursiveTask<Integer>{
    private int n;

    public MyTask(int n){
        this.n = n;
    }

    @Override
    protected Integer compute() {
        if (n == 1) {
            log.debug("join() {}", n);
            return n;
        }

        // 将任务进行拆分(fork)
        MyTask t1 = new MyTask(n - 1);
        t1.fork();
        log.debug("fork() {} + {}", n, t1);

        // 合并(join)结果
        int result = n + t1.join();
        log.debug("join() {} + {} = {}", n, t1, result);
        return result;
    }
}

// 改进
class AddTask3 extends RecursiveTask<Integer>{
    // 起始,结束
    int begin;
    int end;

    public AddTask3(int begin, int end) {
        this.begin = begin;
        this.end = end;
    }


    @Override
    protected Integer compute() {
        if(begin == end){
            return begin;
        }
        if(begin +1 == end){
            return begin + end;
        }

        int mid = (begin + end) /2;
        AddTask3 t1 = new AddTask3(begin,mid);
        t1.fork();
        AddTask3 t2 = new AddTask3(mid+1,end);
        t2.fork();

        int result = t1.join() + t2.join();
        return result;


    }
}
