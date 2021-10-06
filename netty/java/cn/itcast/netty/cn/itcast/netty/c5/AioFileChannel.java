package cn.itcast.netty.cn.itcast.netty.c5;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static cn.itcast.netty.c2_bytebuffer.ByteBufferUtil.debugAll;

/**
 * 测试异步io
 * Created by John.Ma on 2021/10/6 0006 17:02
 */
@Slf4j
public class AioFileChannel {
    public static void main(String[] args) throws IOException {
        try (
            AsynchronousFileChannel channel = AsynchronousFileChannel.open(Paths.get("data.txt"), StandardOpenOption.READ)
        ) {
            // 参数1: ByteBuffer
            // 参数2: 读取的起始位置
            // 参数3: 附件
            // 参数4: 回调对象
            ByteBuffer buffer = ByteBuffer.allocate(16);
            log.debug("read begin...");
            channel.read(buffer, 0, buffer, new CompletionHandler<Integer, ByteBuffer>() {
                // 一次read操作成功后的方法
                @Override
                public void completed(Integer result, ByteBuffer attachment) {
                    log.debug("read completed...{}",result);
                    attachment.flip(); // 切换读模式
                    debugAll(attachment);
                }

                // read过程中出现异常了
                @Override
                public void failed(Throwable exc, ByteBuffer attachment) {
                    exc.printStackTrace();
                }
            });
            log.debug("read end...");
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.in.read();
    }

}
