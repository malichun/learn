package com.hspedu;

/**
 * @author: malichun
 * @date 2022/4/22 0022 17:59
 */
public class Cat {

    private String name = "招财猫";
    public int age = 10;

    public Cat() {
    }

    public Cat(String name) {
        this.name = name;
    }

    public void hi(){
        // System.out.println("hi "+name);
    }

    public void cry(){ // 常用方法
        System.out.println("猫喵喵叫~~");
    }

}
