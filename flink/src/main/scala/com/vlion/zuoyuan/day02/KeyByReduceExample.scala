package com.vlion.zuoyuan.day02

import org.apache.flink.api.common.functions.ReduceFunction
import org.apache.flink.streaming.api.scala._

object KeyByReduceExample {
    def main(args: Array[String]): Unit = {
        val env = StreamExecutionEnvironment.getExecutionEnvironment
        env.setParallelism(1)

        val inputDataStream = env.addSource(new SensorSource)

        inputDataStream
            .keyBy(_.id)
            //第二个字段时间戳为定义
            .reduce((s1,s2) => SensorReading(s1.id,0L,math.min(s1.temperature,s2.temperature)))
            .print()



        env.execute()

    }

}
