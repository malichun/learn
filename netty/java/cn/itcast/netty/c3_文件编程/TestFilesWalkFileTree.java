package cn.itcast.netty.c3_文件编程;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 遍历文件的树
 * Created by John.Ma on 2021/10/4 0004 0:23
 */
public class TestFilesWalkFileTree {
    public static void main(String[] args) throws IOException {
        // 递归删除目录
//        Files.delete(Paths.get("I:\\黑马并发编程 - 副本"));
        // 从里到外删除
        Files.walkFileTree(Paths.get("I:\\黑马并发编程 - 副本"),new SimpleFileVisitor<Path>(){

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
//                System.out.println(file);
                Files.delete(file); // 删除文件
                return super.visitFile(file, attrs);
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
//                System.out.println("<====退出"+dir);
                Files.delete(dir);
                return super.postVisitDirectory(dir, exc);
            }
        });

    }

    /**
     * 统计有多少jar包
     * @throws IOException
     */
    private static void m2() throws IOException {
        AtomicInteger jarCount = new AtomicInteger();
        // 看文件夹有多少jar包
        Files.walkFileTree(Paths.get("D:\\softInstalled\\JAVA8"),new SimpleFileVisitor<Path>(){
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if(file.toString().endsWith(".jar")){
                    System.out.println(file);
                    jarCount.incrementAndGet();
                }
                return super.visitFile(file, attrs);
            }
        });
        System.out.println("jarCount:"+jarCount);
    }

    /**
     * 遍历文件夹和文件
     * @throws IOException
     */
    private static void m1() throws IOException {
        final AtomicInteger dirCount = new AtomicInteger();
        final AtomicInteger fileCount = new AtomicInteger();

        Files.walkFileTree(Paths.get("D:\\softInstalled\\JAVA8"),new SimpleFileVisitor<Path>(){ // 使用访问者模式

            // 进入文件夹之前会执行
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                System.out.println("====>"+dir);
                dirCount.incrementAndGet();
                return super.preVisitDirectory(dir, attrs);
            }

            // 遍历到这个文件执行的方法
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                fileCount.incrementAndGet();
                System.out.println(file);
                return super.visitFile(file, attrs);
            }
        });

        System.out.println("dir count:"+dirCount);
        System.out.println("file count:"+ fileCount);
    }
}
