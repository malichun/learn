package itheima.demo2_genericsclass;

/**
 * Created by John.Ma on 2022/4/13 0013 21:36
 */
public class MainClass {
    public static void main(String[] args) {
        // 泛型类在创建对象的时候,来指定操作的具体数据类型
        Generic<String> strGeneric = new Generic<>("abc");
        String key1 = strGeneric.getKey();
        System.out.println("key1:"+key1);

        System.out.println("----------------------------------");
        Generic<Integer> intGeneric = new Generic<>(100);
        int key2 = intGeneric.getKey();
        System.out.println("key2:"+key2);

        // 泛型类在创建对象的时候,没有指定类型,将按照Object类型来操作
        Generic generic = new Generic(100);
        Object key3 = generic.getKey();
        System.out.println("key3:" + key3);

        // 泛型类不支持基本数据类型
        // Generic<int> generic1 = new Generic<int>(100);


        // 3. 同一泛型类, 根据不同的数据类型创建的对象, 本质上是同一类型
        System.out.println("----------------------------------");
        System.out.println(intGeneric.getClass());
        System.out.println(strGeneric.getClass());

        // true
        System.out.println(intGeneric.getClass() == strGeneric.getClass());

    }
}
