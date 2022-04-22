package itheima.demo4;

/**
 * 泛型类派生子类, 子类也是泛型类, 那么子类的泛型标识要和父类的标识一致
 *
 */
public class Test04 {
    public static void main(String[] args) {
        ChildFirst<String> childFirst = new ChildFirst<>();
        childFirst.setValue("abc");
        String value = childFirst.getValue();
        System.out.println(value);

        System.out.println("-----------------------------");

        ChildSecond childSecond = new ChildSecond();
        childSecond.setValue("aaaa");
        System.out.println(childSecond.getValue());

    }
}
