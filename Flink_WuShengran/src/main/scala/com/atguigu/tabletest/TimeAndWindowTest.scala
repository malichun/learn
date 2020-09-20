package com.atguigu.tabletest

import com.atguigu.apitest.SensorReading
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.table.api.{EnvironmentSettings, Over, Tumble}
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

        //设置时间特性
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)

        val settings = EnvironmentSettings.newInstance()
            .useBlinkPlanner()
            .inStreamingMode()
            .build()
        //表环境
        val tableEnv = StreamTableEnvironment.create(env, settings)

        //读取数据
        val inputPath = "D:\\fileImportant\\Learn_projects\\learn\\Flink_WuShengran\\src\\main\\resources\\sensor.txt"
        val inputStream = env.readTextFile(inputPath)

        //先转换成样例类类型(简单转换操作)
        val dataStream = inputStream
            .map(data => {
                val arr = data.split(",")
                SensorReading(arr(0), arr(1).toLong, arr(2).toDouble)
            })
            .assignTimestampsAndWatermarks(new BoundedOutOfOrdernessTimestampExtractor[SensorReading](Time.milliseconds(1000)) {
                override def extractTimestamp(element: SensorReading): Long = element.timestamp * 1000L
            })

        //        val sensorTable = tableEnv.fromDataStream(dataStream,'id,'temperature,'timestamp,'pt.rowtime)  //'pt.rowtime 提取的时间戳
        val sensorTable = tableEnv.fromDataStream(dataStream, 'id, 'temperature, 'timestamp.rowtime as 'ts)

/** 窗口测试 */

/** Group Window */
        //窗口测试
        //1.Group Window   //需求每10s钟的窗口,根据id分组,每个id有多少值进来,滚动窗口
        //1.1 table api
        val resultTable = sensorTable
            .window(Tumble over 10.seconds on 'ts as 'tw)
            .groupBy('id, 'tw)
            .select('id, 'id.count, 'temperature.avg, 'tw.end) //'tw.end 当前窗口的关窗时间

        //1.2.sql
        tableEnv.createTemporaryView("sensor", sensorTable)
        val resultSqlTable = tableEnv.sqlQuery(
            """
              |
              |select
              |   id,
              |   count(id),
              |   avg(temperature),
              |   tumble_end(ts,interval '10' second)  -- 当前窗口的关窗时间
              |from
              |   sensor
              |group by
              |   id,
              |   tumble(ts, interval '10' second)
              |
              |""".stripMargin)

        //转换成流打印输出
        //        resultTable.toAppendStream[Row].print("result")  //写入就不会更改了
        //        resultSqlTable.toRetractStream[Row].print("sql result")

/** 2.Over Window */
        //2. Over Window:统计每个sensor每条数据,与之前两行数据的平均温度(按照事件时间排序)
        //2.1 table api
        val overResultTable = sensorTable
            .window(Over partitionBy 'id orderBy 'ts preceding 2.rows as 'ow)
                .select('id, 'ts, 'id.count over 'ow, 'temperature.avg over 'ow)

        //2.2 sql
        val overResultSqlTable = tableEnv
                .sqlQuery(
                    """
                      |select
                      |
                      | id,
                      | ts,
                      | count(id) over ow,
                      | avg(temperature) over ow
                      |from
                      | sensor
                      |window ow as (
                      | partition by id
                      | order by ts
                      | rows between 2 preceding and current row
                      |
                      |)
                      |
                      |""".stripMargin)
        overResultTable.toAppendStream[Row].print("result")  //写入就不会更改了
        overResultSqlTable.toRetractStream[Row].print("sql result")


        //        sensorTable.printSchema()
        //root
        // |-- id: STRING
        // |-- temperature: DOUBLE
        // |-- timestamp: BIGINT
        // |-- pt: TIMESTAMP(3) *PROCTIME*

        //        sensorTable.toAppendStream[Row].print()

        env.execute("time and window test")
    }
}
