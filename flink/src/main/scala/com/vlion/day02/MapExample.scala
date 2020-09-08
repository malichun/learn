package com.vlion.day02

import org.apache.flink.api.common.functions.MapFunction
import org.apache.flink.streaming.api.scala._

/**
 * Created by John.Ma on 2020/9/9 0009 0:45
 */
object MapExample {
    def main(args: Array[String]): Unit = {
        val env = StreamExecutionEnvironment.getExecutionEnvironment

        env.setParallelism(1)

        val stream = env.addSource(new SensorSource)

        val mapped1 = stream.map( r => r.id)

        val mapped2 = stream.map(new MyMapFunction())

        val mapped3 = stream.map(new MapFunction[SensorReading,String] {
            override def map(value: SensorReading): String = {
                value.id
            }
        })

        mapped1.print()
        mapped2.print()
        mapped3.print()

        env.execute()
    }

    /**
     * 输入泛型 : SensorReading 输出泛型:String
     */
    class MyMapFunction extends MapFunction[SensorReading,String]{
        override def map(value: SensorReading): String = {
            value.id
        }
    }
}
