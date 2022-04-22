package itheima.demo2_genericsclass;

/**
 * 泛型类的定义
 * @param <T> 泛型标识,-- 类形参
 *           创建对象的时候来指定指定具体的数据类型
 */
public class Generic<T> {

    // T, 是由外部使用类的时候来指定的.
    private T key;

    public void setKey(T key) {
        this.key = key;
    }

    public T getKey() {
        return key;
    }


    public Generic(T key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "Generic{" +
            "key=" + key +
            '}';
    }
}
