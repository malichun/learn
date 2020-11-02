package com.atguigu.tabletest.udftest

import com.atguigu.apitest.SensorReading
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.table.api.EnvironmentSettings
import org.apache.flink.table.api.scala._
import org.apache.flink.table.functions.AggregateFunction
import org.apache.flink.types.Row

/**
 * Created by John.Ma on 2020/9/19 0019 16:24
 */
object AggregateFunctionTest {
    def main(args: Array[String]): Unit = {
        val env = StreamExecutionEnvironment.getExecutionEnvironment
        env.setParallelism(1)
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)  //设置事件时间

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

        val sensorTable = tableEnv.fromDataStream(datastream, 'id, 'temperature,'timestamp.rowtime as 'ts)

        // 1.table api
        val avgTemp = new AvgTemp
        val resultTable =sensorTable
                .groupBy('id)
                .aggregate(avgTemp('temperature) as 'avgTemp)
                .select('id,'avgTemp)

        //2.sql
        tableEnv.createTemporaryView("sensor",sensorTable)
        tableEnv.registerFunction("avgTemp",avgTemp)
        val resultSqlTable = tableEnv.sqlQuery(
            """
              |select
              | id,avgTemp(temperature)
              |from
              | sensor
              |group by id
              |""".stripMargin)

        resultTable.toRetractStream[Row].print("result")
        resultSqlTable.toRetractStream[Row].print("sql")


        datastream.print()



        env.execute()

    }
}

//定义一个类,专门用于表示聚合的状态
class AvgTempAcc {
    var sum:Double = 0.0
    var count:Long = 0L
}

//自定义一个聚合函数
//需求:求每个传感器所有温度的平均值,保存转改(tempSum,tempCount)
class AvgTemp extends AggregateFunction[Double,AvgTempAcc]{ //保存的平均数,保存sum/count

    override def getValue(accumulator: AvgTempAcc): Double = {
        accumulator.sum /accumulator.count
    }

    override def createAccumulator(): AvgTempAcc = new AvgTempAcc

    //还要实现一个具体的处理计算函数accumulate
    def accumulate(accumulator:AvgTempAcc, temp:Double):Unit={ //必须为unit的输出
        accumulator.sum += temp
        accumulator.count += 1
    }
}
