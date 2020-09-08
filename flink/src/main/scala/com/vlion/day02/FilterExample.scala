package com.vlion.day02

import org.apache.flink.api.common.functions.FilterFunction
import org.apache.flink.streaming.api.scala._

/**
 * Created by John.Ma on 2020/9/9 0009 0:56
 */
object FilterExample {
    def main(args: Array[String]): Unit = {
        val env = StreamExecutionEnvironment.getExecutionEnvironment
        env.setParallelism(1)

        val stream = env.addSource(new SensorSource)

        val filtered1 = stream.filter( r => r.id.equals("sensor_1"))

        val filtered2 = stream.filter(new MyFilterFunction)

        val filtered3 = stream.filter(new FilterFunction[SensorReading] {
            override def filter(value: SensorReading): Boolean = {
                value.id.equals("sensor_1")
            }
        })

        filtered1.print()
        filtered2.print()
        filtered3.print()

        env.execute()

    }

    /**
     * filter算子输入和输出的类型是一样的,所以只有一个泛型SensorReading
     */
    class MyFilterFunction extends FilterFunction[SensorReading]{
        override def filter(value: SensorReading): Boolean = {
            value.id.equals("sensor_1")
        }
    }
}
