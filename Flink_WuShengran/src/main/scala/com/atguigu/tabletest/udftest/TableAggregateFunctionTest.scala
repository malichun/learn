package com.atguigu.tabletest.udftest

import com.atguigu.apitest.SensorReading
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.table.api.EnvironmentSettings
import org.apache.flink.table.api.scala._
import org.apache.flink.table.functions.TableAggregateFunction
import org.apache.flink.types.Row
import org.apache.flink.util.Collector

/**
 * @description: 表聚合函数,多 -> 多
 * @author: malichun
 * @time: 2020/9/20/0020 14:26
 *
 */
object TableAggregateFunctionTest {
    def main(args: Array[String]): Unit = {

        val env = StreamExecutionEnvironment.getExecutionEnvironment
        env.setParallelism(1)
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime) //设置事件时间

        val settings = EnvironmentSettings.newInstance()
            .inStreamingMode()
            .useBlinkPlanner()
            .build()

        val tableEnv = StreamTableEnvironment.create(env, settings)

        //先转换成样例类类型
        val datastream = env.readTextFile("E:\\gitdir\\learn_projects\\myLearn\\Flink_WuShengran\\src\\main\\resources\\sensor.txt")
            .map(data => {
                val arr = data.split(",")
                SensorReading(arr(0), arr(1).toLong, arr(2).toDouble)
            })
            //分配时间水位线
            .assignTimestampsAndWatermarks(new BoundedOutOfOrdernessTimestampExtractor[SensorReading](Time.seconds(1)) { //设置watermark 1
                override def extractTimestamp(element: SensorReading): Long = element.timestamp * 1000L
            })

        val sensorTable = tableEnv.fromDataStream(datastream, 'id, 'temperature, 'timestamp.rowtime as 'ts)

        //1. table api
        val top2Temp = new Top2Temp

        val resultTable = sensorTable
            .groupBy('id)
            .flatAggregate(top2Temp('temperature) as ('temp, 'rank))
            .select('id, 'temp, 'rank)

        // 2.sql 不太好用

        resultTable.toRetractStream[Row].print()
        env.execute("table aggregate function test")

    }
}


//定义一个类,用来表示表聚合函数的聚合状态
class Top2TempAcc(
                     var highestTemp: Double = Double.MinValue,
                     var secondHighestTemp: Double = Double.MinValue
                 )

//自定义表聚合函数
// 需求:提取所有温度值中最高的两个温度,输出(temp,rank)
class Top2Temp extends TableAggregateFunction[(Double, Int), Top2TempAcc] {
    override def createAccumulator(): Top2TempAcc = new Top2TempAcc()

    //实现计算聚合结果的函数accumulate
    def accumulate(acc: Top2TempAcc, temp: Double): Unit = {
        //判断当前温度值是否比状态保存的大
        if (temp > acc.highestTemp) {
            acc.secondHighestTemp = acc.highestTemp
            acc.highestTemp = temp
        } else if (temp > acc.secondHighestTemp) { //如果在最高和第二高之间,那么直接替换第二高温度
            acc.secondHighestTemp = temp
        }
    }

    //实现一个输出结果的方法,最终处理完表中所有数据时调用
    def emitValue(acc: Top2TempAcc, out: Collector[(Double, Int)]): Unit = {
        out.collect((acc.highestTemp, 1))
        out.collect((acc.secondHighestTemp, 1))
    }

}
