package cn.itcast.test;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * Created by John.Ma on 2021/7/20 0020 22:01
 * <p>
 * 自定义原子整数
 */
public class Test42 {
    public static void main(String[] args) {
        Account.demo(new MyAtomicInteger(10000));
    }
}

class MyAtomicInteger implements Account{
    private volatile int value;
    static final Unsafe unsafe;
    static final long DATA_OFFSET;

    static {
        unsafe = UnsafeAccessor.getUnsafe();

        try {
            // data 属性在 DataContainer 对象中的偏移量，用于 Unsafe 直接访问该属性
            DATA_OFFSET = unsafe.objectFieldOffset(MyAtomicInteger.class.getDeclaredField("value"));
        } catch (NoSuchFieldException e) {
            throw new Error(e);
        }
    }

    /**
     * 构造器
     * @param value
     */
    public MyAtomicInteger(int value){
        this.value = value;
    }

    public int getValue(){
        return value;
    }


    public void decrease(int amount){
        while(true){
            int prev = this.value;
            int next = prev - amount;
            // 成员变量是基本类型,不是包装类,用Int
            if(unsafe.compareAndSwapInt(this,DATA_OFFSET,prev,next)){
                break;
            }
        }
    }

    @Override
    public Integer getBalance() {
        return getValue();
    }

    @Override
    public void withdraw(Integer amount) {
        decrease(amount);
    }
}

class UnsafeAccessor {
    static Unsafe unsafe;

    static {
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            unsafe = (Unsafe) theUnsafe.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    static Unsafe getUnsafe() {
        return unsafe;
    }
}
