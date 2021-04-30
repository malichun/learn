package com.atguigu.chapter15_GC_CONCEPT.java1;

import java.lang.ref.WeakReference;

/**
 * Created by John.Ma on 2021/4/28 0028 0:01
 */
public class WeakReferenceTest {
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
        // 构造了弱引用
        WeakReference<User> userWeakReference = new WeakReference<>(new User(1, "songhk"));

        //从弱引用中重新获取对象
        System.out.println(userWeakReference.get());

        System.gc();
        //不管当前内存空间是否足够,都会回收它的内存
        System.out.println("After gc:");
        //重新尝试从弱引用中获取对象
        System.out.println(userWeakReference.get());


    }

}
