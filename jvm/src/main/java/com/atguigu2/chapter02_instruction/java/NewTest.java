package com.atguigu2.chapter02_instruction.java;

/**
 * Created by John.Ma on 2021/5/30 0030 10:43
 */
class Order {
    int id;
    static String name;
}

public class NewTest {
    public void setOrderId() {
        Order order = new Order();
        order.id = 1001;
        System.out.println(order.id);

        Order.name = "ORDER";
        System.out.println(Order.name);
    }
}