import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @author: malichun
 * @time: 2021/7/2/0002 17:59
 */
public class BianyiClass {
    public static void main(String[] args) {
        List<String> list = new ArrayList<String>() {
            {
                add("a");
                add("b");
            }
        };
    }
}
