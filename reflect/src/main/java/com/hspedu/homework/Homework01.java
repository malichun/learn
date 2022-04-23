package com.hspedu.homework;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 1.定义PrivateTest类, 有私有name属性, 并且属性值为hellokitty
 * 2. 提供getName的共有方法
 * 3.创建PrivateTest的类, 利用Class类得到私有的name属性, 修改私有的name属性值, 并调用getName()的方法打印name属性值
 * @author: malichun
 * @date 2022/4/24 0024 1:34
 */
public class Homework01 {
    public static void main(String[] args) throws Exception{
        // 1.得到PrivateTest类对应的Class对象
        Class<PrivateTest> privateTestClass = PrivateTest.class;
        // 2.创建对象实例
        PrivateTest privateTestObj = privateTestClass.newInstance();
        // 3. 得到name属性
        Field name = privateTestClass.getDeclaredField("name");
        // 4. 爆破name
        name.setAccessible(true);
        name.set(privateTestObj, "天龙八部");
        // 5. 得到getName方法对象
        Method getName = privateTestClass.getMethod("getName");
        Object invoke = getName.invoke(privateTestObj);
        System.out.println("name属性的值 = " + invoke); //name属性的值 = 天龙八部


    }
}

class PrivateTest{
    private String name = "hellokitty";

    public String getName(){
        return name;
    }
}
