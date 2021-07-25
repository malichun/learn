import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by John.Ma on 2021/7/24 0024 11:50
 */
public class Test5 {
    public static void main(String[] args) {


        ReentrantLock lock = new ReentrantLock();
        Condition condition = lock.newCondition();
    }

}
