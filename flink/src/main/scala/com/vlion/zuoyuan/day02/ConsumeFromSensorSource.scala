package com.vlion.zuoyuan.day02

import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.api.scala._

/**
 * Created by John.Ma on 2020/9/9 0009 0:33
 */
object ConsumeFromSensorSource {
    def main(args: Array[String]): Unit = {

        val env = StreamExecutionEnvironment.getExecutionEnvironment

        env.setParallelism(1)

        //调用addSource方法
        val stream = env.addSource(new SensorSource)

        stream.print()

        env.execute()
    }
}
