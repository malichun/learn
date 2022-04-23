package com.hspedu.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 *
 * 演示如何通过反射获取类的结构信息
 * @author: malichun
 * @date 2022/4/23 0023 23:23
 */
public class ReflectionUtils {
    public static void main(String[] args) {
        method_04();

    }

    // 第一组API
    public static void api_01(){
        // 得到Class对象
        Class<Person> personClass = Person.class;

        // 1.getName: 获取全类名
        System.out.println(personClass.getName()); //com.hspedu.reflection.Person

        // 2. getSimpleName: 获取简单类名
        System.out.println(personClass.getSimpleName()); // Person

        // 3.getFields: 获取所有public修饰的属性, 包含本类以及父类的
        Field[] fields = personClass.getFields();
        Stream.of(fields).forEach(f -> System.out.println("所有public属性:"+f.getName())); //
        // 结果:
        // 所有public属性: name
        // 所有public属性: hobby

        // 4.getDeclaredFields: 获取本类中所有属性
        Field[] declaredFields = personClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            System.out.println("本类中所有属性="+ declaredField.getName());
        }
        // 结果:
        // 本类中所有属性=name
        // 本类中所有属性=age
        // 本类中所有属性=job
        // 本类中所有属性=sal



        // 5. getMethods: 获取所有public修饰的方法, 包含本类以及父类的(所有超类)
        Method[] methods = personClass.getMethods();
        for (Method method : methods) {
            System.out.println("本类及父类的方法="+ method.getName());
        }
        // 本类及父类的方法=m1
        // 本类及父类的方法=hi
        // 本类及父类的方法=wait
        // 本类及父类的方法=wait
        // 本类及父类的方法=wait
        // 本类及父类的方法=equals
        // 本类及父类的方法=toString
        // 本类及父类的方法=hashCode
        // 本类及父类的方法=getClass
        // 本类及父类的方法=notify
        // 本类及父类的方法=notifyAll


        // 6. getDeclaredMethods: 获取本类所有方法
        Method[] declaredMethods = personClass.getDeclaredMethods();
        for (Method method : declaredMethods) {
            System.out.println("本类中所有方法:"+method.getName());
        }
        // 结果:
        // 本类中所有方法:m1
        // 本类中所有方法:m2
        // 本类中所有方法:m3
        // 本类中所有方法:m4

        // 7. getConstructors: 获取所有public修饰的构造器, 只包含本类
        Constructor<?>[] constructors = personClass.getConstructors();
        for (Constructor<?> constructor : constructors) {
            System.out.println("本类public的构造器:"+constructor);
        }
        // 结果
        // 本类public的构造器:public com.hspedu.reflection.Person(java.lang.String)
        // 本类public的构造器:public com.hspedu.reflection.Person()


        // 8.getDeclaredConstructors: 获取本类中所有的构造器
        Constructor<?>[] declaredConstructors = personClass.getDeclaredConstructors();
        for (Constructor<?> declaredConstructor : declaredConstructors) {
            System.out.println("本类中所有的构造器:"+declaredConstructor);
        }
        // 结果
        //本类中所有的构造器:private com.hspedu.reflection.Person(java.lang.String,int)
        // 本类中所有的构造器:public com.hspedu.reflection.Person(java.lang.String)
        // 本类中所有的构造器:public com.hspedu.reflection.Person()


        // 9.getPackage: 以Package形式返回 包信息
        Package aPackage = personClass.getPackage();
        System.out.println("包信息:"+aPackage);
        // 结果:
        // 包信息:package com.hspedu.reflection

        // 10. getSuperClass: 以Class形式返回父类信息
        Class<? super Person> superclass = personClass.getSuperclass();
        System.out.println("获取父类信息"+superclass);
        //  结果:
        //获取父类信息class com.hspedu.reflection.A


        // 11.getInterfaces: 以Class[]形式返回接口信息
        Class<?>[] interfaces = personClass.getInterfaces();
        for (Class<?> anInterface : interfaces) {
            System.out.println("接口信息:"+anInterface);
        }
        //  结果:
        // 接口信息:interface com.hspedu.reflection.IA
        // 接口信息:interface com.hspedu.reflection.IB

        // 12.getAnnotations: 以Annotations[] 形式返回注解信息
        Annotation[] annotations = personClass.getAnnotations();
        for (Annotation annotation : annotations) {
            System.out.println("注解信息:"+annotation);
        }
        // 结果:
        // 注解信息:@java.lang.Deprecated()
    }

    // 第二组API
    public static void api_02(){
        // 得到Class对象
        Class<Person> personClass = Person.class;
        // getDeclaredFields: 获取本类中所有属性
        // 规定 说明: 默认修饰符 是0, public 1, private 是2, protected是4, static是8 , final是16
        Field[] declaredFields = personClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            System.out.println("本类中所有属性="+ declaredField.getName()
                + " 该属性的修饰值" +declaredField.getModifiers()
                + " 该属性的类型=" + declaredField.getType() // 该属性类对应的Class对象
            );
        }
        //结果:
        // 本类中所有属性=name 该属性的修饰值1 该属性的类型=class java.lang.String
        // 本类中所有属性=age 该属性的修饰值4 该属性的类型=int
        // 本类中所有属性=job 该属性的修饰值0 该属性的类型=class java.lang.String
        // 本类中所有属性=sal 该属性的修饰值2 该属性的类型=double
    }

    public static void method_03(){
        // 得到Class对象
        Class<Person> personClass = Person.class;

        // getDeclaredMethods: 获取本类所有方法
        Method[] declaredMethods = personClass.getDeclaredMethods();
        for (Method method : declaredMethods) {
            System.out.println("本类中所有方法:"+method
            + " 该方法的访问修饰符=" + method.getModifiers()
                +" 该方法返回类型= " + method.getReturnType()
                + " 该方法的形参数组" + Arrays.toString(method.getParameterTypes())
            );
        }
    }

    public static void method_04(){
        // 得到Class对象
        Class<Person> personClass = Person.class;

        // 8.getDeclaredConstructors: 获取本类中所有的构造器
        Constructor<?>[] declaredConstructors = personClass.getDeclaredConstructors();
        for (Constructor<?> declaredConstructor : declaredConstructors) {
            System.out.println("本类中所有的构造器:"+declaredConstructor
            + " 构造器参数类型:" + Arrays.toString(declaredConstructor.getParameterTypes())
            );
        }
    }
}

// 父类
class A{
    public String hobby;

    public void hi(){

    }

    // 构造器
    public A(){}
}

// 接口
interface IA{

}
interface IB{

}

@Deprecated
class Person extends A implements IA,IB{
    // 属性
    public String name;
    protected int age;
    String job;
    private double sal;

    // 构造器
    public Person(){}
    public Person(String name){}
    private Person(String name, int age){}

    // 方法
    public void m1(){}
    protected void m2(){}
    void m3(){}
    private void m4(){}
}
