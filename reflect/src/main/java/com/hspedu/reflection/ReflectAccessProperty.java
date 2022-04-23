package com.hspedu.reflection;

import java.lang.reflect.Field;

/**
 * 演示反射操作属性
 * @author: malichun
 * @date 2022/4/24 0024 0:55
 */
public class ReflectAccessProperty {
    public static void main(String[] args) throws Exception{
        // 1.得到Student类对应的Class对象
        Class<Student> studentClass = Student.class;
        // 2.创建对象
        Student student = studentClass.newInstance();
        // 3. 使用反射得到age 属性对象
        Field ageField = studentClass.getField("age");
        ageField.set(student, 88); // 通过反射来操作属性
        System.out.println(student); // Student{age=28, name='null'}

        // 4. 使用反射操作name 属性(私有 静态)
        Field name = studentClass.getDeclaredField("name");
        // 对name进行爆破, 可以操作private属性
        name.setAccessible(true);
        name.set(student, "老韩"); //
        System.out.println(student); //  Student{age=88, name='老韩'}

        // 因为name是static的, 与类相关的, 对象可以设置为null
        name.set(null, "张三丰");
        System.out.println(student); //Student{age=88, name='张三丰'}


        // 获取属性
        System.out.println(name.get(student)); // 张三丰
        // 只有是静态的才可以用
        System.out.println(name.get(null)); // 张三丰

    }
}


class Student{
    public int age;
    private static String name;

    public Student(){}

    @Override
    public String toString() {
        return "Student{" +
            "age=" + age +
            ", name='" + name + '\'' +
            '}';
    }
}
