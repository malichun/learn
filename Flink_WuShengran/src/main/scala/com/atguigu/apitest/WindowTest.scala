package com.atguigu.apitest

import java.util.Properties

import org.apache.flink.api.common.functions.ReduceFunction
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011

/**
 * Created by John.Ma on 2020/9/13 0013 11:59
 */
object WindowTest {
    def main(args: Array[String]): Unit = {
        val env = StreamExecutionEnvironment.getExecutionEnvironment
        env.setParallelism(1)
        //从调用时刻开始给env创建的每一个stream追加时间特征,事件时间
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime) //设置为事件时间

        //读取数据,
        //    val inputStream = env.readTextFile("D:\\fileImportant\\Learn_projects\\learn\\Flink_WuShengran\\src\\main\\resources\\sensor.txt")

        //换成socket流输入
        //        val inputStream = env.socketTextStream("www.bigdata01.com", 4444)
        //kafkaSource
        val properties = new Properties()
        properties.setProperty("bootstrap.servers", "www.bigdata04.com:9092,www.bigdata05.com:9092,www.bigdata06.com:9092")
        properties.setProperty("group.id", "consumer-group")
        properties.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
        properties.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
        properties.setProperty("auto.offset.reset", "latest")

        val inputStream = env.addSource(new FlinkKafkaConsumer011[String]("ad_log", new SimpleStringSchema(), properties))
        //先转换成样例类类型(简单转换操作)
        val dataStream = inputStream
            .filter(line => line != null && line.split(",").length == 3)
            .map(data => {
                val arr = data.split(",")
                SensorReading(arr(0), arr(1).toLong, arr(2).toDouble)
            })
            //添加watermark
            //      .assignAscendingTimestamps(_.timestamp * 1000L)    // 升序数据提取时间戳
            //处理乱序数据
            .assignTimestampsAndWatermarks(new BoundedOutOfOrdernessTimestampExtractor[SensorReading](Time.seconds(3)) {
                override def extractTimestamp(element: SensorReading): Long = {
                    element.timestamp * 1000
                }
            })

        //    val resultStream = dataStream
        //      .map(s => (s.id,s.temperature))
        //      .keyBy(data => data._1)
        ////      .window(TumblingEventTimeWindows.of(Time.seconds(15)))  //滚动时间窗口
        ////      .window(SlidingAlignedProcessingTimeWindows.of(Time.seconds(15),Time.seconds(3))) //滑动时间窗口
        ////      .window(EventTimeSessionWindows.withGap(Time.seconds(10)))  //会话窗口
        //      .timeWindow(Time.days(1)) //简单版
        ////        .countWindow(10)  //滚动计数窗口
        //        .min(1)

        val lateTag = new OutputTag[(String, Double, Long)]("late")
        //每15秒统计一次,窗口内各个传感器温度的最小值,以及最新的时间戳
        val resultStream = dataStream
            .map(s => (s.id, s.temperature, s.timestamp))
            .keyBy(_._1) //按照二元组的第一个元素(id)分组
            //      .window( TumblingEventTimeWindows.of(Time.seconds(15)))    // 滚动时间窗口
            //      .window( SlidingProcessingTimeWindows.of(Time.seconds(15), Time.seconds(3)) )    // 滑动时间窗口
            //      .window( EventTimeSessionWindows.withGap(Time.seconds(10)) )    // 会话窗口
            // countWindow(10)  //滚动计数窗口
            .timeWindow(Time.seconds(15))
            //过了waterMark迟到的数据但是没有超过1分钟,还是会加入计算的,但是会每来一个计算一个,在watermark内的数据是到点一起计算
            .allowedLateness(Time.minutes(1))
            .sideOutputLateData(lateTag)
            //      .minBy(1)
            .reduce((curRes, newData) => (curRes._1, curRes._2.min(newData._2), curRes._3.max(newData._3)))

        resultStream.getSideOutput(lateTag).print("late")
        resultStream.print("result")

        env.execute("window test")

    }
}


class MyReducer extends ReduceFunction[(String, Double, Long)] {

    override def reduce(value1: (String, Double, Long), value2: (String, Double, Long)): (String, Double, Long) = ???
}