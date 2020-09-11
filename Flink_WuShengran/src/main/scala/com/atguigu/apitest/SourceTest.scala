package com.atguigu.apitest

import java.util.Properties

import org.apache.flink.streaming.api.scala._

/**
 * 用在工业物联网,采集传感器数据,温度传感器
 */
case class SensorReading(id:String, timestamp:Long,temperature:Double)

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
        val inputPath="E:\\gitdir\\learn_projects\\myLearn\\Flink_WuShengran\\src\\main\\resources\\sensor.txt"
        val stream2 = env.readTextFile(inputPath)


        //3.从kafka读取数据
        val properties = new Properties()
        properties.put();

        val stream3 = env.addSource()




        stream3.print()


        env.execute()





    }
}
