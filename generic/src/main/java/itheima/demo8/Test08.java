package itheima.demo8;

import java.util.Comparator;
import java.util.TreeSet;

/**
 * Created by John.Ma on 2022/4/14 0014 0:45
 */
public class Test08 {

    public static void main(String[] args) {
        // TreeSet 的构造方法, 下限通配符
        //  public TreeSet(Comparator<? super E> comparator)


        // 使用Cat的比较器,根据age排序
        // TreeSet<Cat> treeSet = new TreeSet<>(new Comparator2());

        // 使用Animal的比较器,根据name排序
        TreeSet<Cat> treeSet = new TreeSet<>(new Comparator1());

        // 报错!!!, 编译不通过
        // TreeSet<Cat> treeSet = new TreeSet<>(new Comparator3());
        treeSet.add(new Cat("jerry", 20));
        treeSet.add(new Cat("amy", 22));
        treeSet.add(new Cat("frank", 35));
        treeSet.add(new Cat("jim", 15));

        for(Cat cat: treeSet){
            System.out.println(cat);
        }



    }
}

/**
 * 比较器1, 根据Animal的name属性比较
 */
class Comparator1 implements Comparator<Animal>{
    @Override
    public int compare(Animal o1, Animal o2) {
        return o1.name.compareTo(o2.name);
    }
}


class Comparator2 implements Comparator<Cat>{
    @Override
    public int compare(Cat o1, Cat o2) {
        return o1.age - o2.age;
    }
}


class Comparator3 implements Comparator<MiniCat>{
    @Override
    public int compare(MiniCat o1, MiniCat o2) {
        return o1.level - o2.level;
    }
}