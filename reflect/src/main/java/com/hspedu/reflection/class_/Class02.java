package com.hspedu.reflection.class_;

import com.hspedu.Car;

import java.lang.reflect.Field;

/**
 * 演示Class类的常用方法
 * @author: malichun
 * @date 2022/4/23 0023 19:01
 */
public class Class02 {
    public static void main(String[] args) throws Exception{
        String classAllPath = "com.hspedu.Car";
        // 1. 获取到Car类对应的Class对象
        // <?> 表示不确定的Java类型
        Class<?> cls = Class.forName(classAllPath);
        // 2. 输出cls
        System.out.println(cls); // 显示cls对象, 是哪个类的Class对象: class com.hspedu.Car
        System.out.println(cls.getClass()); // 输出cls运行的类型 java.lang.Class

        // 3.得到包名
        System.out.println(cls.getPackage().getName()); // com.hsp
        // 4. 得到全类名
        System.out.println(cls.getName()); // com.hspedu.Car
        // 5. 通过cls创建对象实例
        Car car = (Car)cls.newInstance();
        System.out.println(car);
        // 6. 通过反射获取属性 brand
        Field brand = cls.getDeclaredField("brand");
        brand.setAccessible(true);
        System.out.println(brand.get(car)); // 宝马

        // 7. 通过反射给属性赋值
        brand.set(car, "奔驰");
        System.out.println(brand.get(car)); // 奔驰

        System.out.println("====所有字段属性====");
        // 8. 我希望大家可以得到所有的属性
        Field[] declaredFields = cls.getDeclaredFields();
        for(Field f: declaredFields){
            System.out.println(f.getName());
        }

    }
}
