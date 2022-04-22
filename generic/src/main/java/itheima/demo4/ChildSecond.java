package itheima.demo4;

/**
 * 泛型类派生子类, 如果子类不是泛型类, 那么父类要明确数据类型.
 *
 */
public class ChildSecond extends Parent<String> {


    @Override
    public String getValue() {
        return super.getValue();
    }

    @Override
    public void setValue(String value) {
        super.setValue(value);
    }
}
