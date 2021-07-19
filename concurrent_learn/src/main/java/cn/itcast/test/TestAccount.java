package cn.itcast.test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @description:
 * @author: malichun
 * @time: 2021/7/19/0019 10:13
 */
public class TestAccount {
    public static void main(String[] args) {
//        Account.demo(new AccountUnsafe(10000));
        Account.demo(new AccountCas(10000));
    }
}

interface Account {
    // 获取余额
    Integer getBalance();

    // 取款
    void withdraw(Integer amount);

    /**
     * 方法内会启动1000个线程,每个线程做-10的操作
     *
     * @param account
     */
    static void demo(Account account) {
        List<Thread> ts = new ArrayList<>();
        long start = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            ts.add(new Thread(() -> {
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

class AccountCas implements Account{
    private AtomicInteger balance;

    public AccountCas(int balance){
        this.balance = new AtomicInteger(balance);
    }

    @Override
    public Integer getBalance() {
        return balance.get();
    }

    @Override
    public void withdraw(Integer amount) {
//        while(true){
//            int prev = balance.get() ;
//            int next = prev - amount;
//
//            if(balance.compareAndSet(prev,next)){
//                break;
//            }
//        }
     //    可以简化为以下方法
        balance.addAndGet(-1 * amount);
    }
}
