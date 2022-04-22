package itheima.demo8;

/**
 * Created by John.Ma on 2022/4/14 0014 0:22
 */
public class Cat extends Animal {

    public int age;

    public Cat(String name, int age) {
        super(name);
        this.age = age;
    }

    @Override
    public String toString() {
        return "Cat{" +
            "age=" + age +
            ", name='" + name + '\'' +
            '}';
    }
}
