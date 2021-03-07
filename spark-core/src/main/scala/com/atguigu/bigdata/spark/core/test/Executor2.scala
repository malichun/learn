package com.atguigu.bigdata.spark.core.test

import java.io.ObjectInputStream
import java.net.ServerSocket

/**
 * Created by John.Ma on 2021/2/1 0001 22:11
 */
object Executor2 {
    def main(args: Array[String]): Unit = {

        //启动服务
        val server = new ServerSocket(8888)
        println("服务器启动,等待接收数据")

        //等待客户端的连接
        val client = server.accept()
        val in = client.getInputStream
        val objIn = new ObjectInputStream(in)
        val task:SubTask = objIn.readObject().asInstanceOf[SubTask]
        val ints = task.compute()
        println("计算结点[8888]计算的结果为:" + ints)
        objIn.close()
        client.close()
        server.close()


    }

}
