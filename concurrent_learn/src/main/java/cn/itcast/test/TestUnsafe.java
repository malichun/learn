package cn.itcast.test;

import lombok.Data;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * @description:
 * @author: malichun
 * @time: 2021/7/20/0020 18:34
 */
public class TestUnsafe {
    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
        Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
        theUnsafe.setAccessible(true);
        Unsafe unsafe= (Unsafe)theUnsafe.get(null);
        System.out.println(unsafe);


        Teacher t = new Teacher();
        // 1.获取域的偏移地址
        long idOffset = unsafe.objectFieldOffset(Teacher.class.getDeclaredField("id"));;
        long nameOffset = unsafe.objectFieldOffset(Teacher.class.getDeclaredField("name"));

        //2执行cas操作
        unsafe.compareAndSwapInt(t,idOffset, 0,1);
        unsafe.compareAndSwapObject(t,nameOffset,null,"张三");
        System.out.println(t);

    }
}

@Data
class Teacher{
    volatile int id;
    volatile String name;
}
