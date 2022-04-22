package itheima.demo4;

/**
 * 子类的泛型可以扩展
 */
public class ChildFirst<T> extends Parent<T> {
    @Override
    public T getValue() {
        return super.getValue();
    }
}
