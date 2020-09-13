package com.atguigu.apitest

import java.util.Properties

import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.streaming.api.functions.source.SourceFunction
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011

import scala.util.Random

/**
 * 用在工业物联网,采集传感器数据,温度传感器
 */
case class SensorReading(id: String, timestamp: Long, temperature: Double)

object SourceTest {
    def main(args: Array[String]): Unit = {
        //创建执行环境
        val env = StreamExecutionEnvironment.getExecutionEnvironment
        //        env.setParallelism(1)

        //1.从集合中读取数据,跑完就结束了
        val dataList = List(
            SensorReading("sensor_1", 1547718199, 35.8),
            SensorReading("sensor_6", 1547718201, 15.4),
            SensorReading("sensor_7", 1547718202, 6.7),
            SensorReading("sensor_10", 1547718205, 38.1)
        )
        val stream1 = env.fromCollection(dataList)


        //2.从文件中读取数据,跑完就结束了
//        val inputPath = "E:\\gitdir\\learn_projects\\myLearn\\Flink_WuShengran\\src\\main\\resources\\sensor.txt"
//        val stream2 = env.readTextFile(inputPath)


        //3.从kafka读取数据
        val properties = new Properties()
        properties.setProperty("bootstrap.servers", "www.bigdata04.com:9092,www.bigdata05.com:9092,www.bigdata06.com:9092")
        properties.setProperty("group.id", "consumer-group")
        properties.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
        properties.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
        properties.setProperty("auto.offset.reset", "latest")

        val stream3 = env.addSource(new FlinkKafkaConsumer011[String]("hippo_clk2", new SimpleStringSchema(), properties))


        //4.自定义source
//        val stream4 = env.addSource(new MySensorSource)
//        stream4.filter(_.id equals "sensor_1")
//        .print()

        stream3.print()
        env.execute("job")


    }
}

// 自定义SourceFunction
class MySensorSource extends SourceFunction[SensorReading]{

    //flag:表示数据源是否正常运行
    var running = true

    override def cancel(): Unit = running = false

    override def run(ctx: SourceFunction.SourceContext[SensorReading]): Unit = {
        val rand = new Random()

        var curTemp = (1 to 10 ) map {i =>
            ("sensor_"+i,65+rand.nextGaussian()*20)
        }


        while(running){
            //更新温度值
            curTemp = curTemp.map(
                t => (t._1,t._2+rand.nextGaussian())
            )

            //获取当前时间,加入到数据中,调用ctx.collect(T)发送数据
            val curTime = System.currentTimeMillis()

            curTemp.foreach( t =>
                ctx.collect(SensorReading(t._1,curTime,t._2))
            )
            Thread.sleep(100)
        }
    }


}
