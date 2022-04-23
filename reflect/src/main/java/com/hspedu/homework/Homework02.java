package com.hspedu.homework;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 *
 * 1.利用Class类的forName方法得到File类的class对象
 * 2.在控制台打印File类的所有构造器
 * 3.通过newInstance的方法创建File对象, 并创建E:\mynew.txt文件
 * 提示: 创建文件的正常写法如下:
 * File file = new File("d:\\aa.txt");
 * file.createNewFile();
 * @author: malichun
 * @date 2022/4/24 0024 1:44
 */
public class Homework02 {
    public static void main(String[] args) throws Exception{
        Class<?> fileCls = Class.forName("java.io.File");
        //2.得到所有构造器
        Constructor<?>[] declaredConstructors = fileCls.getDeclaredConstructors();
        for (Constructor<?> declaredConstructor : declaredConstructors) {
            System.out.println(declaredConstructor);
        }
        //3. 调用构造器
        Constructor<?> constructor = fileCls.getConstructor(String.class);
        Object o = constructor.newInstance("D:/aa.txt");
        // file的运行类型就是File
        Method createNewFile = fileCls.getMethod("createNewFile");
        Object invoke = createNewFile.invoke(o);
        System.out.println(invoke);
    }
}
