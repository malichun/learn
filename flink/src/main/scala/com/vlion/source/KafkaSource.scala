package com.vlion.source

import java.util.Properties

import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011
import org.apache.flink.api.scala._

object KafkaSource {
    def main(args: Array[String]): Unit = {
        val env = StreamExecutionEnvironment.getExecutionEnvironment

        val properties = new Properties()
        properties.setProperty("bootstrap.servers","www.bigdata04.com:9092,www.bigdata05.com:9092,www.bigdata06.com:9092")
        properties.setProperty("group.id","consumer-group2")
        properties.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
        properties.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
        properties.setProperty("auto.offset.reset", "latest")

        val stream3 = env.addSource(new FlinkKafkaConsumer011[String]("sensor",new SimpleStringSchema(),properties))

        env.execute()


    }
}
