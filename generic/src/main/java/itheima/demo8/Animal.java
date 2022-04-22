package itheima.demo8;

/**
 * Created by John.Ma on 2022/4/14 0014 0:21
 */
public class Animal {
    public String name;

    public Animal(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Animal{" +
            "name='" + name + '\'' +
            '}';
    }
}
