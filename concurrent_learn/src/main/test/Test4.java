/**
 * @description:
 * @author: malichun
 * @time: 2021/7/16/0016 13:52
 */
public class Test4 {
    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
//            LockSupport.park();
            try {
                Thread.sleep(1000000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("打断park后是否会清空打断状态"+Thread.currentThread().isInterrupted());
        },"t1");
        t1.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        t1.interrupt();
    }
}
