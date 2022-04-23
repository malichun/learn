package com.hspedu.reflection.class_;

import com.hspedu.Cat;

/**
 * @author: malichun
 * @date 2022/4/23 0023 18:35
 */
public class Class01 {
    public static void main(String[] args) throws Exception{
        // 看看Class类图
        // 1. Class也是类, , 因此也继承Object
        // Class
        // 2. Class类对象也不是new出来的, 而是系统创建的
        // (1) 传统new对象
        /* ClassLoader类
        public Class<?> loadClass(String name) throws ClassNotFoundException{
            return loadClass(name, false);
        }
         */
        // Cat cat = new Cat();
        // (2) 反射方式,(要把上面注释)
        // 类加载的时候, 也会进入ClassLoader.loadClass(name, false) 方法加载Class对象
        Class<?> aClass = Class.forName("com.hspedu.Cat");

        // 3. 对于某个类的Class类对象, 在内存中只有一份, 因为类只加载一次

        // 4. 每个类的实例都会记得自己是由哪个Class实例所生成

        // 5.  通过Class可以完整的得到是由哪个Class实例所生成

        // 6. Class对象是存放在堆的

        // 7.类的字节码二进制数据, 是放在方法区的, 有的地方称为类的元数据(包括方法代码,变量名, 方法名, 访问权限等等)


    }
}
