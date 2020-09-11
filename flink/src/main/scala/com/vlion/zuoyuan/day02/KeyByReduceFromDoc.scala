package com.vlion.zuoyuan.day02

import org.apache.flink.streaming.api.scala._

object KeyByReduceFromDoc {
    def main(args: Array[String]): Unit = {
        val env = StreamExecutionEnvironment.getExecutionEnvironment
        env.setParallelism(1)

        val inputStream = env
            .fromElements(
                ("en", List("tea")),
                ("fr", List("vin")),
                ("en", List("cake"))
            )

        inputStream
            .keyBy(_._1)
            .reduce((r1,r2) => (r1._1,r1._2 ::: r2._2))
            .print()

        env.execute()
    }
}
