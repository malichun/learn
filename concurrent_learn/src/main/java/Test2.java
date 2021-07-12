/**
 * 使用3个线程循环打印出1-100
 */
public class Test2 {
    public static void main(String[] args) {
        Thread t1 = new Thread(new MyThread1(0));
        Thread t2 = new Thread(new MyThread1(1));
        Thread t3 = new Thread(new MyThread1(2));
        t1.start();
        t2.start();
        t3.start();

    }

    static class MyThread1 implements Runnable{

        private static Object lock = new Object();

        // 静态变量
        private static int count =0;

        int no;
        public MyThread1(int no){
            this.no = no;
        }

        @Override
        public void run() {
            while(true){
                synchronized (lock){
                    if(count >= 100){
                        break;
                    }
                    if(count %3 == this.no){
                        count ++;
                        System.out.println(this.no + "--->" +count);
                    }else{
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    lock.notifyAll();
                }
            }

        }
    }
}
