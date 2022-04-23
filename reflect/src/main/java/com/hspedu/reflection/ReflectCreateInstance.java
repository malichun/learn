package com.hspedu.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 *
 * 演示通过反射创建实例
 * @author: malichun
 * @date 2022/4/24 0024 0:36
 */
public class ReflectCreateInstance {
    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        // 1.先获取到User类的Class对象
        Class<?> userClass = Class.forName("com.hspedu.reflection.User");

        // 2. 通过public 的无参构造器创建实例
        Object o = userClass.newInstance();
        System.out.println(o); // User{age=10, name='韩顺平教育'}

        // 3. 通过public的有参构造器创建实例
        // constructor 对象就是:public User(String name) 这个构造器
        // 先得到对应的构造器, 再传入实参
        Constructor<?> constructor = userClass.getConstructor(String.class);
        Object hsp = constructor.newInstance("hsp");
        System.out.println("hsp= "+hsp); // hsp= User{age=10, name='hsp'}

        // 4. 通过非public的构造器创建实例
        // 4.1先得到private的构造器对象
        Constructor<?> constructor1 = userClass.getDeclaredConstructor(int.class, String.class);
        // 4.2爆破, 使用反射可以访问private构造器
        constructor1.setAccessible(true);
        // 4.3创建实例
        Object o1 = constructor1.newInstance(100, "张三丰"); //User{age=100, name='张三丰'}
        System.out.println(o1);

    }
}

// User类
class User{
    private int age = 10;
    private String name = "韩顺平教育";

    // public的有参构造器
    public User(String name){
        this.name = name;
    }

    // 无参 public
    public User(){

    }

    // 有参, 私有
    private User(int age, String name){
        this.age = age;
        this.name = name;
    }

    @Override
    public String toString() {
        return "User{" +
            "age=" + age +
            ", name='" + name + '\'' +
            '}';
    }
}