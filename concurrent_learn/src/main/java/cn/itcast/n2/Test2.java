package cn.itcast.n2;

import org.openjdk.jol.info.ClassLayout;

/**
 * Created by John.Ma on 2021/7/11 0011 22:40
 */
public class Test2 {
    public static void main(String[] args) {
        Object o = new Object();
        System.out.println(ClassLayout.parseInstance(o).toPrintable());
        synchronized (o) {
            System.out.println(ClassLayout.parseInstance(o).toPrintable());
        }
    }
}
