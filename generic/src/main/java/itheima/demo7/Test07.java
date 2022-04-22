package itheima.demo7;

/**
 * Created by John.Ma on 2022/4/14 0014 0:07
 */
public class Test07 {
    public static void main(String[] args) {
        Box<Number> box1 = new Box<>();
        box1.setFirst(100);
        showBox(box1);

        Box<Integer> box2 = new Box<>();
        box2.setFirst(200);
        // 这边可以通过
        showBox(box2);
    }

    /**
     * ?代表通配符, 使用上界
     * @param box 任意类型
     */
    public static void showBox(Box<? extends Number> box){
        Number first = box.getFirst();
        System.out.println(first);
    }
}
