package com.atguigu.chapter08_heap.java;

/**
 * @description:
 * @author: malichun
 * @time: 2021/3/16/0016 16:08
 */
public class SimpleHeap {
    private int id;

    public SimpleHeap(int id){
        this.id = id;
    }

    public void show(){
        System.out.println("My ID is " + id);
    }

    public static void main(String[] args) {
        SimpleHeap s1 = new SimpleHeap(1);
        SimpleHeap s2 = new SimpleHeap(2);

        int[] arr = new int[10];
        Object[] arr1 = new Object[10];
    }
}
