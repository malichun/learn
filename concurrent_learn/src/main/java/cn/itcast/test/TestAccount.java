package cn.itcast.test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntUnaryOperator;

/**
 * Created by John.Ma on 2021/7/17 0017 22:48
 */
public class TestAccount {
    public static void main(String[] args) {
        Account.demo(new AccountUnsafe(10000));

        Account.demo(new AccountCas(10000));
    }
}

class AccountCas implements Account {
    // 基于无锁的实现
    private AtomicInteger balance;

    public AccountCas(int balance){
        this.balance = new AtomicInteger(balance);
    }

    @Override
    public Integer getBalance() {
        return this.balance.get();
    }

    @Override
    public void withdraw(Integer amount) {
        while(true){
            // 获取余额的最新值
            int prev = balance.get();
            // 修改后的余额
            int next = prev - amount;
            // 真正修改
            if( balance.compareAndSet(prev, next)){ // 如果修改成功
                break;
            }
        }
    }
}

interface Account {
    // 获取余额
    Integer getBalance();

    // 取款
    void withdraw(Integer amount);

    /**
     * 方法内会启动1000个线程,每个线程做-10元的操作
     * 如果初始余额为10000那么正确结果应该是0
     * @param account
     */
    static void demo(Account account){
        List<Thread> ts = new ArrayList<>();
        long start = System.nanoTime();
        for(int i =0;i<1000;i++){
            ts.add(new Thread(() ->{
                account.withdraw(10);
            }));
        }
        ts.forEach(Thread::start);
        ts.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        long end = System.nanoTime();
        System.out.println(account.getBalance() + " cost: " + (end - start) / 1000_000 + " ms");
    }
}

class AccountUnsafe implements Account {
    private Integer balance;

    public AccountUnsafe(Integer balance) {

        this.balance = balance;

    }

    @Override
    public Integer getBalance() {
        synchronized (this) {
            return balance;
        }
    }

    @Override
    public void withdraw(Integer amount) {
        synchronized (this) {
            this.balance -= amount;
        }
    }
}
