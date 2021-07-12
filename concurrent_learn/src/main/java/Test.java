import lombok.extern.slf4j.Slf4j;
//使用两个线程循环打印出1~100
@Slf4j(topic = "c.Test")
public class Test {
    public static void main(String[] args) {
        Num num = new Num();
        Thread t1 = new Thread(new A(num));
        Thread t2 = new Thread(new B(num));
        t1.start();
        t2.start();
    }
}

    class A implements Runnable{
        private Num num;
        public A(Num num){
            this.num = num;
        }

        @Override
        public void run() {
            while(num.i<99){
                synchronized (num){
                    if(num.flag){
                        try {
                            num.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    num.i++;
                    num.flag = true;
                    System.out.println(Thread.currentThread() + " "+ num.i);
                    num.notify();
                }
            }
        }
    }

    class B implements Runnable{
        private Num num;
        B(Num num){
            this.num=num;
        }

        @Override
        public void run() {
            while(num.i<99){
                synchronized (num){
                    if(!num.flag){
                        try {
                            num.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    num.i++;
                    num.flag=false;
                    System.out.println(Thread.currentThread()+" "+num.i);
                    num.notify();
                }
            }
        }
    }


class Num{
    int i;
    boolean flag;
}
