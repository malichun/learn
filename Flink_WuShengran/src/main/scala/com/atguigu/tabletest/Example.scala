package com.atguigu.tabletest

import java.io.InputStream

import com.atguigu.apitest.SensorReading
import org.apache.flink.streaming.api.scala._
import org.apache.flink.table.api.Table
import org.apache.flink.table.api.scala._

/**
 * Created by John.Ma on 2020/9/15 0015 21:39
 */
object Example {
    def main(args: Array[String]): Unit = {
        val env = StreamExecutionEnvironment.getExecutionEnvironment
        env.setParallelism(1)


        val resource = getClass.getResource("/sensor.txt")

        //读取数据
        val inputStream = env.readTextFile(resource.getPath)

        //先转换成样例类类型(简单转换操作)
        val dataStream = inputStream
            .map(data => {
                val arr = data.split(",")
                SensorReading(arr(0),arr(1).toLong,arr(2).toDouble)
            })


        // 首先创建表执行环境
        val tableEnv = StreamTableEnvironment.create(env)

        //基于流创建一张表
        val dataTable:Table = tableEnv.fromDataStream(dataStream)

        //调用table api 进行转换
        val resultTable = dataTable
                .select("id,temperature")
                .filter("id == 'sensor_1'")

        // 直接用sql实现
        tableEnv.createTemporaryView("dataTable",dataTable)
        val sql:String = "select id,temperature from dataTable where id='sensor_1'"

        val resultSqlTable: Table = tableEnv.sqlQuery(sql)


        resultTable.toAppendStream[(String, Double)].print("result")
        resultSqlTable.toAppendStream[(String, Double)].print("sql result")

        env.execute("table api example")

    }
}
