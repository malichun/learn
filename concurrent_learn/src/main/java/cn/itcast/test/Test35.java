package cn.itcast.test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @author: malichun
 * @time: 2021/7/19/0019 13:49
 */
public class Test35 {
    public static void main(String[] args) {
        DecimalAccount.demo(new DecimalAccountUnsafe(new BigDecimal(10000)));
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

// 安全实现,使用锁
class DecimalAccountSafeLock implements DecimalAccount{
    private final Object lock = new Object();

    @Override
    public BigDecimal getBalance() {
        return null;
    }

    @Override
    public void withdraw(BigDecimal amount) {

    }
}