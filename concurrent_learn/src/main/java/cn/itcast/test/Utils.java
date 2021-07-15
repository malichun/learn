package cn.itcast.test;

/**
 * @description:
 * @author: malichun
 * @time: 2021/7/15/0015 11:57
 */
public class Utils {
    public static void sleep(long seconds){
        try {
            Thread.sleep(1000 * seconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
