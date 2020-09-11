package com.vlion.zuoyuan.day02

import org.apache.flink.api.common.functions.FlatMapFunction
import org.apache.flink.streaming.api.scala._
import org.apache.flink.util.Collector

/**
 * Created by John.Ma on 2020/9/9 0009 1:38
 */
object FlatMapExample1 {
    def main(args: Array[String]): Unit = {

        val env = StreamExecutionEnvironment.getExecutionEnvironment
        env.setParallelism(1)

        val stream = env.addSource(new SensorSource)

        //使用flatMap实现map功能
        stream.flatMap(new FlatMapFunction[SensorReading,String] {
            override def flatMap(value: SensorReading, out: Collector[String]): Unit = {
                out.collect(value.id)
            }
        }).print()

        //使用flatMap实现filter功能
        stream.flatMap(new FlatMapFunction[SensorReading,SensorReading] {
            override def flatMap(value: SensorReading, out: Collector[SensorReading]): Unit = {
                if(value.id.equals("sensor_1")) {
                    out.collect(value)
                }
            }
        }).print

        env.execute()
    }
}
