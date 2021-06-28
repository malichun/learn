package com.atguigu3.chapter03_gui;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: malichun
 * @time: 2021/6/22/0022 11:28
 */
public class MapTest {
    static Map wMap = new WeakHashMap();
    static Map map = new HashMap();

    public static void main(String[] args) throws InterruptedException {
        init();
        System.out.println("-----------------------------------");
        testWeakHashMap();
        System.out.println("-----------------------------------");
        testHashMap();
    }

    public static void init(){
        String ref1 = new String("object1");
        String ref2 = new String("object2");
        String ref3 = new String("object3");
        String ref4 = new String("object4");
        wMap.put(ref1,"cacheObject1");
        wMap.put(ref2,"cacheObject2");
        map.put(ref3,"cacheObject3");
        map.put(ref4,"cacheObjecdt4");
        System.out.println("String 引用ref1,ref2,ref3,ref4消失");
    }

    public static void testWeakHashMap() throws InterruptedException{
        System.out.println("WeakHashMap GC之前");
        for(Object o:wMap.entrySet()) System.out.println(o);
        System.gc();
        TimeUnit.SECONDS.sleep(2);
        System.out.println("WeakHashMap GC之后");
        for(Object o:wMap.entrySet()) System.out.println(o);
    }

    public static void testHashMap() throws InterruptedException{
        System.out.println("HashMap GC之前");
        for(Object o : map.entrySet()) System.out.println(o);
        System.gc();
        TimeUnit.SECONDS.sleep(2);
        System.out.println("HashMap GC之后");
        for(Object o: map.entrySet()) System.out.println(o);
    }

}
