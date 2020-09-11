package com.vlion.day02

import org.apache.flink.streaming.api.functions.co.CoFlatMapFunction
import org.apache.flink.streaming.api.scala._
import org.apache.flink.util.Collector

object CoFlatMapExample {
    def main(args: Array[String]): Unit = {
        val env = StreamExecutionEnvironment.getExecutionEnvironment
        //        env.setParallelism(1)

        println(env.getParallelism) //打印默认并行度

        val stream1: DataStream[(String,Int)] = env
            .fromElements( ("zuoyuan", 130),
                ("baiyuan", 100)
            )
            .setParallelism(1)

        val stream2: DataStream[(String,Int)] = env
            .fromElements(
                ("zuoyuan", 35),
                ("baiyuan", 33)
            )
            .setParallelism(1)

        //将key相同的联合在一起
        val connected: ConnectedStreams[(String, Int), (String, Int)] = stream1.keyBy(_._1)
            .connect(stream2.keyBy(_._1))

        val printed: DataStream[String] = connected.flatMap(new MyCoFlatMap )

        printed.print()

        env.execute()

    }

    class MyCoFlatMap extends CoFlatMapFunction[(String, Int), (String, Int),String]{
        override def flatMap1(value: (String, Int), out: Collector[String]): Unit = {
            out.collect(value._1+"的体重是:"+value._2+"斤")
            out.collect(value._1+"的体重是:"+value._2+"斤")
        }

        override def flatMap2(value: (String, Int), out: Collector[String]): Unit = {
            out.collect(s"${value._1}的年龄是:${value._2}岁")
        }
    }

}
