package com.vlion.wc

import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment

object StreamWordCount {
    def main(args: Array[String]): Unit = {

        //从外部命令获取参数
        val params = ParameterTool.fromArgs(args)

        val host= params.get("host")
        val port = params.getInt("port")

        //创建流处理环境
        val env = StreamExecutionEnvironment.getExecutionEnvironment

        //接收socket文本流
        val textDstream = env.socketTextStream(host,port)

        //flatMap和Map都需要引入隐式转换
        import org.apache.flink.api.scala._
        val dataStream = textDstream.flatMap(_.split("\\W+"))
            .filter(_.nonEmpty)
            .map((_,1))  //def map[R: TypeInformation](fun: T => R): DataStream[R] = {
            .keyBy(0)
            .sum(1)

        dataStream.print().setParallelism(1)

        //启动executor ,执行任务
        env.execute("Socket stream word count")

    }
}
