package com.atguigu3.chapter02_console;

import java.util.Map;
import java.util.Set;

/**
 * @description:
 * @author: malichun
 * @time: 2021/6/10/0010 16:34
 *
 * Java层面追踪当前进程中的所有的线程
 */
public class AllStackTrace {
    public static void main(String[] args) {
        Map<Thread,StackTraceElement[]> all = Thread.getAllStackTraces();
        Set<Map.Entry<Thread, StackTraceElement[]>> entries = all.entrySet();
        for(Map.Entry<Thread,StackTraceElement[]> en: entries){
            Thread t = en.getKey();
            StackTraceElement[] v = en.getValue();
            System.out.println("[Thread name is :"+ t.getName() + "]");
            for(StackTraceElement s:v){
                System.out.println("\t"+s.toString());
            }
        }

    }
}
