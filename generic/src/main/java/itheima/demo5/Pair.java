package itheima.demo5;

/**
 * 泛型类实现泛型接口
 */
public class Pair<T, E> implements Generator<T> {

    private T key;
    private E value;

    public Pair(T key, E value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public T getKey() {
        return key;
    }

    public E getValue(){
        return value;
    }

}
