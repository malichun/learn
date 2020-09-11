package com.vlion.zuoyuan.day02

import org.apache.flink.streaming.api.scala._

object KeyByExample {
    def main(args: Array[String]): Unit = {
        val env = StreamExecutionEnvironment.getExecutionEnvironment
        env.setParallelism(1)

        val stream = env
            .addSource(new SensorSource) //-> DataStream[T]
            .keyBy(_.id)    //KeyedStream[T,K]
            .min(2)

        stream.print()
        env.execute()
    }

}
