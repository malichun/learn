package com.hspedu.reflection;

import java.lang.reflect.Method;

/**
 * 演示通过反射调用方法
 * @author: malichun
 * @date 2022/4/24 0024 1:12
 */
public class ReflectAccessMethod {
    public static void main(String[] args) throws Exception{
        // 1.得到Boss类对应的Class对象
        Class<Boss> bossClass = Boss.class;
        // 2.创建对象
        Boss o = bossClass.newInstance();
        // 3. 调用public的hi方法
        // 3.1得到hi方法对象
        Method hi = bossClass.getMethod("hi", String.class);
        // 3.1 调用
        hi.invoke(o, "韩顺平教育"); // hi 韩顺平教育

        // 4.调用private static 方法
        // 4.1得到 say方法对象
        Method say = bossClass.getDeclaredMethod("say", int.class, String.class, char.class);
        say.setAccessible(true);
        Object return_ = say.invoke(o, 100, "张三", '男');
        System.out.println(return_); //100 张三 男
        // 4.3 因为say方法是static, 对象可以传入null
        Object res = say.invoke(null, 200, "李四", '女');
        System.out.println(res); // 200 李四 女

        // 5. 在反射中, 如果方法有返回值, 统一返回Object, 运行类型和方法定义返回的类型一致
        System.out.println(res.getClass()); // class java.lang.String


    }
}

class Boss{
    public int age;
    private static String name;

    public Boss(){}

    // 私有 静态 方法
    private static String say(int n, String s, char c){
        return n + " " + s + " " + c;
    }

    // 普通方法
    public void hi(String s){
        System.out.println("hi "+ s);
    }
}