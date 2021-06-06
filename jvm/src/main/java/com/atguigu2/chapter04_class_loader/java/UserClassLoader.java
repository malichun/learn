package com.atguigu2.chapter04_class_loader.java;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by John.Ma on 2021/6/6 0006 10:15
 */
public class UserClassLoader extends ClassLoader{
    private String rootDir;

    public UserClassLoader(String rootDir) {
        this.rootDir = rootDir;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        //获取类的class文件字节数组
        byte[] classData = getClassData(classNameToPath(name));
        if(classData == null){
            throw new ClassNotFoundException();
        }else{
            //直接生成class对象
            return defineClass(name,classData,0,classData.length);
        }
    }

    /**
     * 编写获取class文件并转换为字节码流的逻辑
     *
     */
    private byte[] getClassData(String className){
        try{
            InputStream ins = new FileInputStream(className);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            //读取类文件的字节码
            while((len = ins.read(buffer)) != -1){
                baos.write(buffer,0,len);
            }
            return baos.toByteArray();

        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 类文件的完全路径
     */
    private String classNameToPath(String className){
        String path = rootDir + "\\" + className.replace(".","\\")+".class";
        System.out.println(path);
        return path;
    }

    public static void main(String[] args) {
        String rootDir = "D:\\fileImportant\\Learn_projects\\learn\\jvm\\target\\classes";
        try {
            //创建自定义的类的加载器1
            UserClassLoader loader1 = new UserClassLoader(rootDir);
            Class clazz1 = loader1.findClass("com.atguigu2.chapter04_class_loader.java.User");

            //创建自定义的类的加载器2
            UserClassLoader loader2 = new UserClassLoader(rootDir);
            Class clazz2 = loader2.findClass("com.atguigu2.chapter04_class_loader.java.User");

            System.out.println(clazz1 == clazz2); // clazz1 与 clazz2对应了不同的类模板结构
            System.out.println(clazz1.getClassLoader());
            System.out.println(clazz2.getClassLoader());

            //#######################
            Class clazz3 = ClassLoader.getSystemClassLoader().loadClass("com.atguigu2.chapter04_class_loader.java.User");
            System.out.println(clazz3.getClassLoader());

            System.out.println(clazz1.getClassLoader().getParent());

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
}
