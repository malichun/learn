import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: malichun
 * @time: 2021/7/2/0002 17:49
 */
public class Test {

    public static void main(String[] args) {
        Map<String,String> map2 = new HashMap<String,String>(){
            @Override
            public String put(String var1, String var2) {
                var1 = "key_value"; // a行
                var2 = "重写后的值";
                return super.put(var1, var2);
            }
        };

        map2.put("start_key","不知道start_key是不是这个");
        System.out.println("找到start_key的值：      "+map2.get("start_key"));
        System.out.println("虽然没有明显把key_value当key赋值，尝试尝试key_value的值：      "+map2.get("key_value"));

        List<String> list = new ArrayList<String>(){
            // 构造代码块,每次调用构造器之前都会执行
            {
                add("a");
                add("b");
            }
        };

    }

}
