package com.hspedu;

/**
 * @author: malichun
 * @date 2022/4/23 0023 19:01
 */
public class Car {
    private String brand = "宝马";
    public int price = 500000;
    public String color = "白色" ;

    @Override
    public String toString() {
        return "Car{" +
            "brand='" + brand + '\'' +
            ", price=" + price +
            ", color='" + color + '\'' +
            '}';
    }
}
