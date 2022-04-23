package com.hspedu.reflection;

import com.hspedu.reflection.question.ReflectionQuestion;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Properties;

/**
 * @author: malichun
 * @date 2022/4/23 0023 13:55
 */
public class Reflection01 {
    public static void main(String[] args) throws Exception{
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

        // java.lang.reflect.Field: 代表列的成员变量, Field对象表示某个类的成员变量
        // 得到name字段
        // getField不能得到私有的属性
        Field ageField = cls.getField("age");
        System.out.println(ageField.getInt(o));

        //java.lang.reflect.Constructor: 代表类的构造方法, Constructor对象表示构造器
        Constructor<?> constructor = cls.getConstructor(); // () 中可以指定构造器参数类型
        System.out.println(constructor); // Cat

        Constructor<?> constructor2 = cls.getConstructor(String.class);
        System.out.println(constructor2);

    }
}
