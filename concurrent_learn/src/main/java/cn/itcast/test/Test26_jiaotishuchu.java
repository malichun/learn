package cn.itcast.test;

/**
 * @description: 线程1输出 a 5次,线程2输出b 5次, 线程3输出c 5次, 现在要求输出 abcabcabcabcabc怎么实现
 * @author: malichun
 * @time: 2021/7/15/0015 17:17
 */
// 1.用wait notify实现
public class Test26_jiaotishuchu {
    public static void main(String[] args) {


    }
}

/**
输出内容, 等待标记          下一个标记
    a       1 条件满足       2
    b       2               3
    c       3               1
 */
class WaitNotify{
    // 等待标记
    private int flag; // 1

    // 循环次数
    private int loopNumber;

    public WaitNotify(int flag, int loopNumber) {
        this.flag = flag;
        this.loopNumber = loopNumber;
    }

    // 打印方法,不同线程调用
    public void print(String str, int waitFlag, int nextFlag){
        for (int i = 0; i < loopNumber; i++) {
            synchronized (this){
                while(flag!= waitFlag){ // 条件不成立
                    try {
                        this.wait(); // 条件不成立,等待
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.print(str); //输出线程
                // 修改等待标记
                flag = nextFlag;
                // 把当前等待的线程都叫醒
                this.notifyAll();

            }
        }
    }
}