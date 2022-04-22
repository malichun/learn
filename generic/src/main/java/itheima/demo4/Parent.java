package itheima.demo4;

/**
 * Created by John.Ma on 2022/4/13 0013 22:35
 */
public class Parent<E> {
    private E value;

    public E getValue() {
        return value;
    }

    public void setValue(E value) {
        this.value = value;
    }
}
