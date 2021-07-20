package cn.itcast.test;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/**
 * @description:
 * @author: malichun
 * @time: 2021/7/20/0020 10:22
 *
 * 原子操作,字段更新器
 */
@Slf4j(topic = "c.Test40")
public class Test40 {
    public static void main(String[] args) {
        Student stu = new Student();

        AtomicReferenceFieldUpdater<Student, String> updater =
            AtomicReferenceFieldUpdater.newUpdater(Student.class, String.class, "name");

        updater.compareAndSet(stu,null,"张三");

        System.out.println(stu);


    }
}

class Student {
    volatile String name;

    @Override
    public String toString() {
        return "Student{" +
            "name='" + name + '\'' +
            '}';
    }
}