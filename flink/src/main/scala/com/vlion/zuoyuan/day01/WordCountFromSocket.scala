package com.vlion.day01

import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.windowing.time.Time

object WordCountFromSocket {
    def main(args: Array[String]): Unit = {
        //获取运行时环境,类似SparkContext
        val env = StreamExecutionEnvironment.getExecutionEnvironment
        //并行任务的数量设置为1
        env.setParallelism(1)


        val stream = env.socketTextStream("www.bigdata01.com",4444,'\n')

        //对数据流机型转换算子操作
        val textStream = stream
        //使用空格来进行切割输入流中的字符串
            .flatMap(_.split("\\s+"))
            //做map操作, w => (w,1)
            .map((_,1))
            //使用word字段进行分组操作,也就是shuffle
            .keyBy(0)
            //分流后的媒体条流上,开5s的滚动窗口
            .timeWindow(Time.seconds(5))
            //做聚合操作,类似于reduce
            .sum(1)

        //将数据流输出到标准输出,也就是打印
        textStream.print()

        //不要忘记执行
        env.execute()
    }
}
