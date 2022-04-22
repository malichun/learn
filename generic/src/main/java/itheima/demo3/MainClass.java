package itheima.demo3;

/**
 * Created by John.Ma on 2022/4/13 0013 22:16
 */
public class MainClass {
    public static void main(String[] args) {
        // 创建抽奖器对象,指定数据类型
        ProductGetter<String> stringProductGetter = new ProductGetter<>();
        String[] strProducts = {"苹果手机", "华为手机", "扫地机器人", "咖啡机"};

        // 给抽奖器中填充奖品
        for (int i = 0; i < strProducts.length; i++) {
            stringProductGetter.addProduct(strProducts[i]);
        }

        // 抽奖
        String product1 = stringProductGetter.getProduct();
        System.out.println("恭喜您, 您抽中了: "+ product1);


    }
}
