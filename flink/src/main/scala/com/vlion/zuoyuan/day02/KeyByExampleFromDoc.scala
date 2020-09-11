package com.vlion.day02

import org.apache.flink.streaming.api.scala._

object KeyByExampleFromDoc {
    def main(args: Array[String]): Unit = {
        val env = StreamExecutionEnvironment.getExecutionEnvironment
        env.setParallelism(1)

        val inputStream = env.fromElements(
            (1, 2, 2), (2, 3, 1), (2, 2, 4), (1, 5, 3))

        val resultStream : DataStream[(Int, Int, Int)]=inputStream
            .keyBy( t => t._2==2)  //使用第一个元素进行分组
            .sum(1) //累加元组第二个元素,其他字段还是第一次出现时的字段

        resultStream.print()
        env.execute()

    }
}
