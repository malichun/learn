package com.atguigu.tabletest

import com.atguigu.apitest.SensorReading
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.table.api.{EnvironmentSettings, Over, Table, Tumble}
import org.apache.flink.table.api.scala._
import org.apache.flink.types.Row

/**
 * @description:
 * @author: malichun
 * @time: 2020/9/16/0016 17:39
 *
 */
object TimeAndWindowTest {
    def main(args: Array[String]): Unit = {
        val env = StreamExecutionEnvironment.getExecutionEnvironment
        env.setParallelism(1)
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)  //设置事件时间

        val setting = EnvironmentSettings.newInstance()
            .useBlinkPlanner()
            .inStreamingMode()
            .build()

        val tableEnv = StreamTableEnvironment.create(env,setting)

        //读取数据
        val inputPath = "E:\\gitdir\\learn_projects\\myLearn\\Flink_WuShengran\\src\\main\\resources\\sensor.txt"

        val inputStream = env.readTextFile(inputPath)

        //先转换成样例类类型
        val dataStream = inputStream
            .map(data => {
                val arr = data.split(",")
                SensorReading(arr(0),arr(1).toLong,arr(2).toDouble)
            })
            //确定时间watermark
            .assignTimestampsAndWatermarks(new BoundedOutOfOrdernessTimestampExtractor[SensorReading](Time.seconds(1)) {
                override def extractTimestamp(element: SensorReading): Long = {
                    element.timestamp * 1000L
                }
            })

        val sensorTable: Table = tableEnv.fromDataStream(dataStream,'id,'temperature,'timestamp.rowtime as 'ts)

//group By window ///////////////
        //1.Group Window
        //1.1 table api
        val resultTable: Table = sensorTable
            .window(Tumble over 10.seconds on 'ts as 'tw) //每10秒统计一次,滚动时间窗口
            .groupBy('id,'tw)
            .select('id, 'id.count, 'temperature.avg, 'tw.end)

        //1.2 sql
        tableEnv.createTemporaryView("sensor",sensorTable)
        val resultSqlTable = tableEnv.sqlQuery(
            """
              |select
              | id,
              | count(id),
              | avg(temperature),
              | tumble_end(ts,interval '10' second)
              |from sensor
              |group by
              | id,
              | tumble(ts,interval '10' second)
              |
              |""".stripMargin)


////over window////////////

        //2.Over window: 统计每个sensor每条数据,与之前两行数据的平均温度
        //2.1 table api
        val overResultTable = sensorTable
            .window(Over partitionBy 'id orderBy 'ts preceding 2.rows as 'ow)
            .select('id, 'ts, 'id.count over 'ow,'temperature.avg over 'ow)

        //2.2sql
        val overResultSqlTable = tableEnv.sqlQuery(
            """
              |select
              |     id,
              |     ts,
              |     count(id) over ow,
              |     avg(temperature) over ow
              |from
              |     sensor
              |window ow as (
              |     partition by id
              |     order by ts
              |     rows between 2 preceding and current row
              |)
              |""".stripMargin
        )

        //转换成流输出
        overResultTable.toAppendStream[Row].print("result")
        overResultSqlTable.toRetractStream[Row].print("sql")

//        sensorTable.printSchema()
//        sensorTable.toAppendStream[Row].print()
        env.execute("time and window test")

    }
}
