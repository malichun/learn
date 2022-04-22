package itheima.demo3;

import java.util.ArrayList;
import java.util.Random;

/**
 * 抽奖器
 * Created by John.Ma on 2022/4/13 0013 22:18
 */
public class ProductGetter<T> {
    static Random random = new Random();

    /**
     * 奖品
     */
    private T product;

    /**
     * 奖品集合
     */
    ArrayList<T> list = new ArrayList<>();

    /**
     * 添加奖品
     */
    public void addProduct(T t){
        list.add(t);
    }

    /**
     * 抽奖
     * @return
     */
    public T getProduct(){
        product = list.get(random.nextInt(list.size()));
        return product;
    }

    /**
     * 定义泛型方法
     * @param list 参数
     * @param <E> 泛型标识, 具体类型由调用方法的时候指定
     * @return
     */
    public <E> E getProduct(ArrayList<E> list){
        return list.get(random.nextInt(list.size()));
    }


    /**
     * 静态的泛型方法, 采用多个泛型类型
     * @param t
     * @param e
     * @param k
     * @param <T>
     * @param <E>
     * @param <K>
     */
    public static <T,E,K> void printType(T t,E e,K k){
        System.out.println(t + "\t" + t.getClass().getSimpleName());
        System.out.println(e + "\t" + e.getClass().getSimpleName());
        System.out.println(k + "\t" + k.getClass().getSimpleName());
    }
}
