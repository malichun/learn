package com.atguigu.wc

import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.streaming.api.scala._

/**
 * Created by John.Ma on 2020/9/10 0010 22:36
 * 流处理Word Count
 */
object StreamWordCount {
    def main(args: Array[String]): Unit = {
        //创建流处理的执行环境
        val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment

        env.setParallelism(1)

        //接收一个socket文本流
        val inputDataStream = env.socketTextStream("172.16.197.86", 4444)

        inputDataStream
            .flatMap(_.split(" "))
            .filter(_ nonEmpty)
            .map((_,1))
            .keyBy(_._1)
            .sum(1)
            .print()

        //启动任务执行
        env.execute("stream word count")

        //结果解释: 2> 前面的数字是一个当前统计的,当前并行任务的号码,4个线程,多线程
        //多线程并行度是4,当前机器的核数
        //2> (hello,16)
        //2> (hello,17)
        //2> (hello,18)
        //2> (hello,19)
        //2> (hello,20)

    }

}
