package com.atguigu.tabletest.udftest

import com.atguigu.apitest.SensorReading
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.table.api.EnvironmentSettings
import org.apache.flink.table.api.scala._
import org.apache.flink.table.functions.TableFunction
import org.apache.flink.types.Row

/**
 * @description:
 * @author: malichun
 * @time: 2020/9/16/0016 18:43
 *
 */
object TableFunctionTest {
    def main(args: Array[String]): Unit = {
        val env = StreamExecutionEnvironment.getExecutionEnvironment
        env.setParallelism(1)
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)

        val settings = EnvironmentSettings.newInstance()
            .inStreamingMode()
            .useBlinkPlanner()
            .build()

        val tableEnv = StreamTableEnvironment.create(env,settings)

        val dataStream = env
            .readTextFile("D:\\fileImportant\\Learn_projects\\learn\\Flink_WuShengran\\src\\main\\resources\\sensor.txt")
            .map((data:String) =>{
                val arr = data.split(",")
                SensorReading(arr(0),arr(1).toLong,arr(2).toDouble)
            })
            .assignTimestampsAndWatermarks(new BoundedOutOfOrdernessTimestampExtractor[SensorReading](Time.seconds(1)){
                override def extractTimestamp(element: SensorReading): Long = {
                    element.timestamp * 1000L
                }
            })

        val sensorTable = tableEnv.fromDataStream(dataStream,'id,'temperature,'timestamp.rowtime as 'ts)

        //1.table api
        val split = new Split("_") //new一个UDF实例
        val resultTable = sensorTable
            .joinLateral(split('id) as ('word,'length) ) //侧向连接
            .select('id,'ts,'word,'length)

        //2.sql
        tableEnv.createTemporaryView("sensor",sensorTable)
        tableEnv.registerFunction("split",split)
        val resultSqlTable = tableEnv.sqlQuery(
            """
              |select
              | id,ts,word,length
              |from
              | sensor, lateral table(split(id)) as splitid(word,length)
              |""".stripMargin)

        resultTable.toAppendStream[Row].print("result")
        resultSqlTable.toAppendStream[Row].print("sql")
        env.execute("tableFunctiontest")

        //结果:
//        result> sensor_1,2019-01-17T09:43:19,sensor,6
//        result> sensor_1,2019-01-17T09:43:19,1,1
//        sql> sensor_1,2019-01-17T09:43:19,sensor,6
//        sql> sensor_1,2019-01-17T09:43:19,1,1
//        result> sensor_6,2019-01-17T09:43:21,sensor,6
//        result> sensor_6,2019-01-17T09:43:21,6,1

    }
}

//自定义tableFunction
class Split(separator:String) extends TableFunction[(String,Int)]{
    def eval(str:String):Unit={
        str.split(separator).foreach(
            word => collect((word,word.length))
        )
    }
}

