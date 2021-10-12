package cn.itcast.nio.c2_bytebuffer;

import java.nio.ByteBuffer;

import static cn.itcast.nio.c2_bytebuffer.ByteBufferUtil.debugAll;

/**
 * 网络上有多条数据发送给服务端，数据之间使用 \n 进行分隔
 * 但由于某种原因这些数据在接收时，被进行了重新组合，例如原始数据有3条为
 *      Hello,world\n
 *      I'm zhangsan\n
 *      How are you?\n
 * 变成了下面的两个 byteBuffer (黏包，半包)
 *      Hello,world\nI'm zhangsan\nHo
 *      w are you?\n
 * 现在要求你编写程序，将错乱的数据恢复成原始的按 \n 分隔的数据
 *
 * Created by John.Ma on 2021/10/3 0003 18:21
 */
public class TestByteBufferExam {
    public static void main(String[] args) {
        ByteBuffer source = ByteBuffer.allocate(32);
        // 第一次接收到的是2条完整的和一条被截断的消息
        source.put("Hello,world\nI'm zhangsan\nHo".getBytes());
        split(source);
        source.put("w are you?\n".getBytes());
        split(source);

    }

    private static void split(ByteBuffer source){
        source.flip(); // 切换为读模式
        for (int i = 0; i < source.limit(); i++) {
            if(source.get(i) == '\n'){
                // 搜到了一条完整的消息
                if(source.get(i) == '\n'){
                    int length = i + 1 -source.position(); // 消息的长度
                    // 把这条消息存入新的ByteBuffer
                    ByteBuffer target = ByteBuffer.allocate(length);
                    // 从source 读,向target中写
                    for(int j = 0;j<length;j++){
                        target.put(source.get());
                    }
                    debugAll(target);
                }
            }
        }

        source.compact();
    }
}
