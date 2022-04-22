package itheima.demo8;

import java.util.ArrayList;

/**
 * Created by John.Ma on 2022/4/14 0014 0:21
 */
public class TestUp {
    public static void main(String[] args) {
        ArrayList<Animal> animals = new ArrayList<>();
        ArrayList<Cat> cats = new ArrayList<>();
        ArrayList<MiniCat> miniCats = new ArrayList<>();

        // addAll(Collection<? extends E> c)
        // 编译通过
        cats.addAll(new ArrayList<MiniCat>());


        // showAnimal(animals); 编译不通过
        showAnimal(cats);
        showAnimal(miniCats);

    }

    /**
     * 泛型上限通配符, 传递的集合类型,只能是Cat或者Cat的子类类型
     * @param list
     */
    public static void showAnimal(ArrayList<? extends Cat> list){
        // 不能填充元素
        // list.add(new Cat()); // 编译不通过
        // list.add(new MiniCat()); // 编译不通过
        // list.add(new Animal()); // 编译不通过
        for (int i = 0; i < list.size(); i++) {
            Cat cat = list.get(i);
            System.out.println(cat);
        }
    }
}
