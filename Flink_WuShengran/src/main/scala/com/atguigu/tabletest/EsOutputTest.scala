package com.atguigu.tabletest

import org.apache.flink.streaming.api.scala._
import org.apache.flink.table.api.{DataTypes, Table}
import org.apache.flink.table.api.scala._
import org.apache.flink.table.descriptors.{Csv, Elasticsearch, FileSystem, Json, Schema}

/**
 * @description:
 * @author: malichun
 * @time: 2020/9/16/0016 18:19
 *
 */
object EsOutputTest {
    def main(args: Array[String]): Unit = {
        //1.创建环境
        val env = StreamExecutionEnvironment.getExecutionEnvironment
        env.setParallelism(1)

        val tableEnv = StreamTableEnvironment.create(env)

        //2.连接外部系统,读取数据,注册表
        val filePath = "E:\\gitdir\\learn_projects\\myLearn\\Flink_WuShengran\\src\\main\\resources\\sensor.txt"

        tableEnv.connect(new FileSystem().path(filePath))
            .withFormat(new Csv())
            .withSchema(new Schema()
                .field("id", DataTypes.STRING())
                .field("timestamp", DataTypes.BIGINT())
                .field("temp", DataTypes.DOUBLE())
            )
            .createTemporaryTable("inputTable")

        //3.转换操作
        val sensorTable: Table = tableEnv.from("inputTable")
        //3.1简单转换
        val resultTable = sensorTable
            .select('id, 'temp)
            .filter('id === "sensor_1")

        //3.2聚合转换
        val aggTable = sensorTable
            .groupBy('id) //基于id分组
            .select('id, 'id.count as 'count)

        //4.输出到es
        tableEnv.connect(new Elasticsearch()
            .version("6")
            .host("localhost", 9200, "http")
            .index("sensor")
            .documentType("temperature")
        )
            .inUpsertMode()
            .withFormat(new Json())
            .withSchema(new Schema()
                .field("id", DataTypes.STRING())
                .field("count", DataTypes.BIGINT())
            )
            .createTemporaryTable("esOutputTable")

        aggTable.insertInto("esOutputTable")

        env.execute("es output test")

    }
}
