package com.vlion.day02

import org.apache.flink.streaming.api.scala._

object UnionExample {
    def main(args: Array[String]): Unit = {
        val env = StreamExecutionEnvironment.getExecutionEnvironment
        env.setParallelism(1)

        //传感器ID为sensor_1的数据来自巴黎的流
        val parisStream = env
            .addSource(new SensorSource(500))
            .filter(r => r.id.equals("sensor_1"))

        //传感器ID为sensor_2的数据来自东京的流
        val tokyoStream = env
            .addSource(new SensorSource)
            .filter(_.id.equals("sensor_2"))

        //传感器ID为sensor_2的数据来自里约的流
        val rioStream = env
            .addSource(new SensorSource(400))
            .filter(_.id.equals("sensor_3"))

        val allCities = parisStream
            .union(tokyoStream, rioStream)

        allCities.print()

        env.execute()

    }
}
