package com.hspedu.reflection.question;

import java.lang.reflect.Method;
import java.util.Properties;

/**
 * @author: malichun
 * @date 2022/4/22 0022 18:07
 */
public class ReflectionQuestion {
    public static void main(String[] args) throws Exception {
        // 根据配置文件re.properties 指定信息, 创建Cat对象并调用方法hi

        // 传统方式
        // Cat cat = new Cat();
        // cat.hi();

        // 我们尝试做一做 -> 明白反射的价值
        // 1.使用Properties 类, 可以读写配置文件
        Properties properties = new Properties();
        properties.load(ReflectionQuestion.class.getClassLoader().getResourceAsStream("re.properties"));
        String classfullpath = properties.getProperty("classfullpath");
        String methodName = properties.getProperty("method");
        System.out.println("classfullpath=" + classfullpath);
        System.out.println("method="+methodName);

        // 3. 使用反射机制
        // (1) 加载类, 返回Class类型的对象
        Class<?> cls = Class.forName(classfullpath);
        // (2) 通过cls对象,得到你加载的类 com.hspedu.Cat 的对象实例
        Object o = cls.newInstance();
        // (3) 通过 cls 得到你加载的类 com.hspedu.Cat 的 methodName 方法对象
        //     即: 在反射中, 可以把方法视为对象(万物皆对象)
        Method method1 = cls.getMethod(methodName);
        // (4) 通过method1 调用: 即通过方法对象来实现调用方法
        method1.invoke(o); // 传统方法 对象.方法(), 反射机制 方法.invoke(对象)

    }
}
