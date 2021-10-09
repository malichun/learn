package cn.itcast.netty.c3_文件编程;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * 拷贝多级目录
 * Created by John.Ma on 2021/10/4 0004 1:05
 */
public class TestFileCopy {
    public static void main(String[] args) throws IOException {
        String source = "I:\\黑马并发编程";
        String target = "I:\\黑马并发编程aaa"; // 目的目录

        Files.walk(Paths.get(source)).forEach(path-> {
            try{
                String targetName = path.toString().replace(source,target);
                //判断是文件还是目录
                // 是目录
                if(Files.isDirectory(path)){
                    Files.createDirectory(Paths.get(targetName));
                }
                // 是普通文件
                else if(Files.isRegularFile(path)){
                    Files.copy(path,Paths.get(targetName));
                }
            }catch (IOException e){
                e.printStackTrace();
            }

        });
    }
}
