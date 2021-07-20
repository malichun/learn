package cn.itcast.test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @description:
 * @author: malichun
 * @time: 2021/7/19/0019 13:49
 */
public class Test35 {
    public static void main(String[] args) {
//        DecimalAccount.demo(new DecimalAccountUnsafe(new BigDecimal(10000)));
//        DecimalAccount.demo(new DecimalAccountSafeLock(new BigDecimal(10000)));
        DecimalAccount.demo(new DecimalAccountSafeCas(new BigDecimal(10000)));
    }
}


interface DecimalAccount{
    // 获取余额
    BigDecimal getBalance();

    // 取款
    void withdraw(BigDecimal amount);

    static void demo(DecimalAccount account){
        List<Thread> ts = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            ts.add(new Thread(() -> {
                account.withdraw(BigDecimal.TEN);
            }));
        }
        ts.forEach(Thread::start);
        ts.forEach( t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        System.out.println(account.getBalance());
    }

}

/**
 * 有线程安全问题的写法
 */
class DecimalAccountUnsafe implements DecimalAccount{
    BigDecimal balance;

    public DecimalAccountUnsafe(BigDecimal balance){
        this.balance = balance;
    }

    @Override
    public BigDecimal getBalance() {
        return balance;
    }

    @Override
    public void withdraw(BigDecimal amount) {
        BigDecimal balance = this.getBalance();
        this.balance = balance.subtract(amount);
    }
}

/**
 * 安全实现,使用锁
 */
class DecimalAccountSafeLock implements DecimalAccount{

    private final Object lock = new Object();
    BigDecimal balance;

    public DecimalAccountSafeLock(BigDecimal balance) {
        this.balance = balance;
    }

    @Override
    public BigDecimal getBalance() {
        return balance;
    }

    @Override
    public void withdraw(BigDecimal amount) {
        synchronized (lock){
            BigDecimal balance = this.getBalance();
            this.balance = balance.subtract(amount);
        }
    }
}

/**
 * 使用原子引用
 */
class DecimalAccountSafeCas implements DecimalAccount{
    private AtomicReference<BigDecimal> ref;

    public DecimalAccountSafeCas(BigDecimal balance) {
        this.ref = new AtomicReference<>(balance);
    }

    @Override
    public BigDecimal getBalance() {
        return this.ref.get();
    }

    @Override
    public void withdraw(BigDecimal amount) {
        while(true){
            BigDecimal prev = ref.get();
            BigDecimal next = prev.subtract(amount);
            if(ref.compareAndSet(prev,next)){
                break;
            }
        }
    }
}