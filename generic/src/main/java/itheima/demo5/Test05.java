package itheima.demo5;

/**
 * Created by John.Ma on 2022/4/13 0013 23:07
 */
public class Test05 {
    public static void main(String[] args) {
        Apple apple = new Apple();
        String key = apple.getKey();
        System.out.println(key);

        System.out.println("-----------------------------------");
        Pair<String, Integer> pair = new Pair<>("count", 100);
        String key1 = pair.getKey();
        System.out.println(key1);

        Integer value = pair.getValue();
        System.out.println(value);


    }
}
