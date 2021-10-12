package cn.itcast.nio.c2_bytebuffer;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author ：malichun
 * @date 2021/9/30 10:42 上午
 * @description：
 * @modified By：Å
 */
@Slf4j
public class TestByteBuffer {
    public static void main(String[] args) {
        String path = TestByteBuffer.class.getClassLoader().getResource("data.txt").getPath();
        // FileChannel
        // 1.输入输出流  2.RandomAccesFile
        try(FileChannel channel = new FileInputStream(path).getChannel()){
            // 准备缓冲区
            ByteBuffer buffer = ByteBuffer.allocate(10);
            while(true){
                // 从channel读取数据, 向buffer写入
                int len = channel.read(buffer); // 从channel读,向buffer写
                log.debug("读取到的字节{}", len);
                if(len == -1){ // 没有内容了
                    break;
                }
                // 打印buffer的内容
                buffer.flip(); // 翻转/打开的意思,切换读模式
                while(buffer.hasRemaining()){
                    byte b = buffer.get();
                    log.debug("实际字节{}",(char)b);
                }
                buffer.clear(); // buffer切换为写模式
            }



        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
