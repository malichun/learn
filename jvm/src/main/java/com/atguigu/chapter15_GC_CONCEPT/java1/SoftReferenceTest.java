package com.atguigu.chapter15_GC_CONCEPT.java1;

import java.lang.ref.SoftReference;

/**
 * 软引用的测试：内存不足即回收
 *
 * @author shkstart  shkstart@126.com
 * @create 2020  16:06
 */
public class SoftReferenceTest {
    public static class User {
        public User(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int id;
        public String name;

        @Override
        public String toString() {
            return "[id=" + id + ", name=" + name + "] ";
        }
    }

    public static void main(String[] args) {
        // 创建对象,建立软引用
//        SoftReference<User> userSoftRef = new SoftReference<>(new User(1,"sonhk"));
        //上面一行代码,等价于如下的三行代码
        User u1 = new User(1,"shkstart");
        SoftReference<User> userSoftReference = new SoftReference<>(u1);
        u1 = null; // 取消强引用

        // 从软引用中重新获取强引用对象
        System.out.println(userSoftReference.get());

        System.gc();
        System.out.println("After GC:");
        //垃圾回收之后获得软引用中的对象
        System.out.println(userSoftReference.get()); //由于堆空间内存足够,所以不会回收软引用的可达对象。
        try {
            byte[] b = new byte[1024 * 1024 * 7];
        }catch (Throwable e){
            e.printStackTrace();
        }finally {
            // 再次从软引用中获取数据
            System.out.println(userSoftReference.get());//在报OOM之前，垃圾回收器会回收软引用的可达对象。
        }

    }

}