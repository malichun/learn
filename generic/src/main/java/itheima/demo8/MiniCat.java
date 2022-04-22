package itheima.demo8;

/**
 * Created by John.Ma on 2022/4/14 0014 0:22
 */
public class MiniCat extends Cat {

    public int level;

    public MiniCat(String name, int age, int level) {
        super(name, age);
        this.level = level;
    }

    @Override
    public String toString() {
        return "MiniCat{" +
            "level=" + level +
            ", age=" + age +
            ", name='" + name + '\'' +
            '}';
    }
}
