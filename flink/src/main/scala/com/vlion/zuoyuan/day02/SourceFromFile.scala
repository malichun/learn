package com.vlion.day02

import org.apache.flink.streaming.api.scala._

object SourceFromFile {

    def main(args: Array[String]): Unit = {
        val env = StreamExecutionEnvironment.getExecutionEnvironment
        env.setParallelism(1)

        val stream = env.readTextFile("'")
            .map(r => {
                //使用,切割
                val arr = r.split(",")
                SensorReading(arr(0),arr(1).toLong,arr(2).toDouble)
            })

        stream.print()
        env.execute()
    }

}
