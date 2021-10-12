package cn.itcast.nio.c3_文件编程;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Created by John.Ma on 2021/10/3 0003 19:14
 */
public class TestFileChannelTransferTo {
    public static void main(String[] args) {
        try(
            FileChannel from = new FileInputStream("data.txt").getChannel();
            FileChannel to = new FileOutputStream("to.txt").getChannel();
        ){
            // 参数1.起始位置 2.传多少数据 3.到什么地方  2g数据
            long size = from.size();
            // 还剩余多少字节没有传送
            for(long left = size;left>0;){
                left -= from.transferTo(size - left,left,to);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
