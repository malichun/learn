package com.atguigu.apitest

import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment

object ProcessFunctionTest {
    def main(args: Array[String]): Unit = {
        val env = StreamExecutionEnvironment.getExecutionEnvironment
        env.setParallelism(1)

        // 读取数据
        val inputStream = env.socketTextStream("localhost", 7777)



    }
}


