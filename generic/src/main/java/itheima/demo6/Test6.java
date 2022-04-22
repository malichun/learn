package itheima.demo6;

import itheima.demo3.ProductGetter;

import java.util.ArrayList;

/**
 * Created by John.Ma on 2022/4/13 0013 23:22
 */
public class Test6 {
    public static void main(String[] args) {
        ProductGetter<Integer> productGetter = new ProductGetter<>();
        int[] products = {100,200,400};
        for (int i = 0; i < products.length; i++) {
            productGetter.addProduct(products[i]);
        }

        // 泛型类的成员方法
        Integer product1 = productGetter.getProduct();
        System.out.println(product1+"\t"+product1.getClass().getSimpleName());
        System.out.println("-----------------------------------------------------");

        ArrayList<String> strList = new ArrayList<>();
        strList.add("笔记本电脑");
        strList.add("苹果手机");
        strList.add("扫地机器人");

        // 泛型方法的调用, 类型是通过调用方法的时候来指定的.
        String product = productGetter.getProduct(strList);
        System.out.println(product+"\t"+product.getClass().getSimpleName());
        System.out.println("-----------------------------------------------------");
        // 调用多个泛型类型的静态泛型方法
        ProductGetter.printType(100, "java", true);

    }
}
