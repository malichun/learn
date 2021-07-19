package cn.itcast.test;

/**
 * Created by John.Ma on 2021/7/17 0017 11:30
 * // 测试单例
 */
public class TestSingleton {

}

class Singleton{
    private Singleton(){}

    private static volatile Singleton INSTANCE = null;

    public static Singleton getInstance(){
        if(INSTANCE == null){
            synchronized (Singleton.class){
                if(INSTANCE == null){
                    INSTANCE = new Singleton();
                }
            }
        }
        return INSTANCE;
    }
}