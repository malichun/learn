package com.vlion.source

import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.api.scala._

object Sensor {
    def main(args: Array[String]): Unit = {
        val env = StreamExecutionEnvironment.getExecutionEnvironment

        val stream1 = env.fromCollection(List(
            SensorReading("sensor_1", 1547718199, 35.80018327300259),
            SensorReading("sensor_6", 1547718201, 15.402984393403084),
            SensorReading("sensor_7", 1547718202, 6.720945201171228),
            SensorReading("sensor_10", 1547718205, 38.101067604893444)
        ))

        stream1.print("stream:").setParallelism(1)

        env.execute()

    }
}

case class SensorReading(id: String, timestamp: Long, temperature: Double)
