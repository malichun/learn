package com.hspedu.reflection.class_;

import com.hspedu.Car;

/**
 * 演示得到Class对象
 * @author: malichun
 * @date 2022/4/23 0023 19:20
 */
public class GetClass_ {
    public static void main(String[] args) throws Exception{
        // 方式1. Class.forName
        String classAllPath = "com.hspedu.Car"; // 通过配置文件读取
        Class<?> cls1 = Class.forName(classAllPath);
        System.out.println("通过Class.forName得到:" + cls1);

        // 方式2. 类名.class, 应用场景: 用于参数传递
        Class<Car> cls2 = Car.class;
        System.out.println("通过Car.class得到 "+cls1);

        // 方式3: 对象.getClass(), 应用场景, 有对象实例
        Car car = new Car();
        Class<?> cls3 = car.getClass();
        System.out.println(cls3);

        // 方式4: 通过类加载器[4种]来获取类的Class对象
        // (1)先得到类加载器car
        ClassLoader classLoader = car.getClass().getClassLoader();
        // (2)通过类加载器得到Class对象
        Class<?> cls4 = classLoader.loadClass(classAllPath);
        System.out.println(cls4);

        // cls1, cls2, cls3, cls4 是同一个对象

        // 5.基本数据(int, char, boolean , float, double, byte, long, short)按如下方式得到Class类对象
        Class<Integer> integerClass = int.class;
        Class<Character> characterClass = char.class;
        Class<Boolean> booleanClass = boolean.class;
        System.out.println(integerClass); // int

        // 6.基本数据类型对应的包装类, 可以通过.type 得到Class类对象
        Class<Integer> type = Integer.TYPE;
        Class<Character> type1 = Character.TYPE;
        System.out.println(type); // int
    }
}
