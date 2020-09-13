package com.atguigu.apitest.sinktest

import java.util.Properties

import com.atguigu.apitest.SensorReading
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.connectors.kafka.{FlinkKafkaConsumer011, FlinkKafkaProducer011}

/**
 * Created by John.Ma on 2020/9/13 0013 1:06
 */
object KafkaSinkTest {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)
    //读取数据
    val inputStream = env.readTextFile("D:\\fileImportant\\Learn_projects\\learn\\Flink_WuShengran\\src\\main\\resources\\sensor.txt")

    //3.从kafka读取数据
    val properties = new Properties()
    properties.setProperty("bootstrap.servers", "www.bigdata04.com:9092,www.bigdata05.com:9092,www.bigdata06.com:9092")
    properties.setProperty("group.id", "consumer-group")
    properties.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    properties.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    properties.setProperty("auto.offset.reset", "latest")

    val stream3 = env.addSource(new FlinkKafkaConsumer011[String]("hippo_clk2", new SimpleStringSchema(), properties))



    /**
     * 先转换成样例类类型(简单转换操作)
     */
    val dataStream = inputStream
      .map(data => {
        val arr = data.split(",")
        SensorReading(arr(0), arr(1).toLong, arr(2).toDouble).toString
      })



    stream3.addSink(new FlinkKafkaProducer011[String]("www.bigdata04.com:9092,www.bigdata05.com:9092,www.bigdata06.com:9092","ad_log",new SimpleStringSchema()))


    env.execute()

  }
}
