package com.atguigu.apitest

import org.apache.flink.api.common.functions.ReduceFunction
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.windowing.assigners.{EventTimeSessionWindows, SlidingAlignedProcessingTimeWindows, TumblingEventTimeWindows, TumblingProcessingTimeWindows}
import org.apache.flink.streaming.api.windowing.time.Time

/**
 * Created by John.Ma on 2020/9/13 0013 11:59
 */
object WindowTest {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)
    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)

    //读取数据
    //    val inputStream = env.readTextFile("D:\\fileImportant\\Learn_projects\\learn\\Flink_WuShengran\\src\\main\\resources\\sensor.txt")

    //换成socket流输入
    val inputStream = env.socketTextStream("www.bigdata01.com", 4444)

    /**
     * 先转换成样例类类型(简单转换操作)
     */
    val dataStream = inputStream
      .map(data => {
        val arr = data.split(",")
        SensorReading(arr(0), arr(1).toLong, arr(2).toDouble)
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

    //每15秒统计一次,窗口内各个传感器温度的最小值,以及最新的时间戳
    val resultStream = dataStream
      .map(s => (s.id, s.temperature, s.timestamp))
      .keyBy(_._1) //按照二元组的第一个元素(id)分组
      .window(TumblingProcessingTimeWindows.of(Time.seconds(15)))
      //        .min("_2")
      .reduce((s1, s2) => (s1._1, s1._2.min(s2._2), s1._3.max(s2._3)))

    resultStream.print()


    env.execute()

  }
}


class MyReducer extends ReduceFunction[(String, Double, Long)] {

  override def reduce(value1: (String, Double, Long), value2: (String, Double, Long)): (String, Double, Long) = ???
}