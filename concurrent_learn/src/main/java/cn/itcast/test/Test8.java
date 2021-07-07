package cn.itcast.test;

import java.util.concurrent.TimeUnit;

/**
 * Created by John.Ma on 2021/7/7 0007 22:13
 */
public class Test8 {

    public static void main(String[] args) {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
