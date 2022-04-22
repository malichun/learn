package itheima.demo8;

import java.util.ArrayList;
import java.util.List;

/**
 * 测试下界
 */
public class TestDown {
    public static void main(String[] args) {
        ArrayList<Animal> animals = new ArrayList<>();
        ArrayList<Cat> cats = new ArrayList<>();
        ArrayList<MiniCat> miniCats = new ArrayList<>();

        // 编译通过
        showAnimal(animals);
        // 编译通过
        showAnimal(cats);
        // 编译不通过
        // showAnimal(miniCats);



    }

    /**
     * 类型通配符的下限, 要求集合只能是Cat或Cat的父类类型
     * @param list
     */
    public static void showAnimal(List<? super Cat> list){
        // 编译通过
        list.add(new Cat("",1));
        // 可以添加子类型
        list.add(new MiniCat("",1,2));

        // 只能拿Object接收
        for (Object o : list) {
            System.out.println(o);
        }
    }
}
