package com.atguigu.tabletest

import org.apache.flink.streaming.api.scala._
import org.apache.flink.table.api.{DataTypes, Table}
import org.apache.flink.table.api.scala._
import org.apache.flink.table.descriptors.{Csv, FileSystem, Kafka, Schema}

/**
 * Created by John.Ma on 2020/9/15 0015 22:43
 */
object TableApiTest {
    def main(args: Array[String]): Unit = {
        //1.创建环境
        val env = StreamExecutionEnvironment.getExecutionEnvironment
        env.setParallelism(1)

        //

        val tableEnv = StreamTableEnvironment.create(env)


        /*
        //选取不同的planner,

            //1.1 基于老版本planner的流处理
                val settings = EnvironmentSettings.newInstance()
                    .useOldPlanner()
                    .inStreamingMode()
                    .build()

                val oldStreamTableEnv = StreamTableEnvironment.create(env,settings)

            //1.2基于老版本的批处理
                val batchEnv = ExecutionEnvironment.getExecutionEnvironment
                val oldBatchTableEnv = BatchTableEnvironment.create(batchEnv)

            //1.3基于blink planner的流处理,统一了
                val blinkStreamSettings = EnvironmentSettings.newInstance()
                    .useBlinkPlanner()
                    .inStreamingMode()
                    .build()

                val blinkStreamTableEnv = StreamTableEnvironment.create(env,blinkStreamSettings)

            //1.4基于blink planner的批处理
                val blinkBatchSettings = EnvironmentSettings.newInstance()
                    .useBlinkPlanner()
                    .inBatchMode()
                    .build()

                val blinkBatchTablEnv = TableEnvironment.create(blinkBatchSettings)
        */

        //2.连接外部系统,读取数据,注册表
        //2.1读取文件
        val filePath = getClass.getResource("/sensor.txt").getPath

        tableEnv.connect(new FileSystem().path(filePath))
            .withFormat(new Csv())
            .withSchema(new Schema()
                .field("id", DataTypes.STRING())
                .field("timestamp", DataTypes.BIGINT())
                .field("temperature", DataTypes.DOUBLE())
            )
            .createTemporaryTable("inputTable")

        //2.2从kafka读取数据
        tableEnv.connect(new Kafka()
            .version("0.11")
            .topic("sensor")
            .property("zookeeper.connect", "localhost:2181")
            .property("bootstrap.servers", "localhost:9092")
        )
            .withFormat(new Csv())
            .withSchema(new Schema()
                .field("id", DataTypes.STRING())
                .field("timestamp", DataTypes.BIGINT())
                .field("temperature", DataTypes.DOUBLE())
            )
            .createTemporaryTable("kafkaInputTable")

        //3.查询替换
        //3.1 使用table api,先得到Table对象
        val sensorTable: Table = tableEnv.from("inputTable")
        val resultTable = sensorTable
            .select('id, 'temperature)
            .filter('id === "sensor_1")

        //3.2 SQL
        val resultSqlTable = tableEnv.sqlQuery(
            """
              |select id, temperature
              |from inputTable
              |where id = 'sensor_1'
              |""".stripMargin)

        //        val inputTable = tableEnv.from("kafkaInputTable")
        //        inputTable.toAppendStream[(String,Long,Double)].print()

        resultTable.toAppendStream[(String, Double)].print("result")
        resultSqlTable.toAppendStream[(String, Double)].print("sql")

        env.execute("table api test")
    }

}
