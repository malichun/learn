package com.hspedu.reflection.proxydemo.aop;

/**
 * @author: malichun
 * @date 2022/4/23 0023 16:19
 */
public class GunDog implements Dog {

    @Override
    public void info() {
        System.out.println("我是一只猎狗");
    }

    @Override
    public void run() {
        System.out.println("我奔跑迅速");
    }
}
