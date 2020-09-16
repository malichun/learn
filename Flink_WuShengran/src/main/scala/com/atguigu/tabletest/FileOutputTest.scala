package com.atguigu.tabletest

import org.apache.flink.streaming.api.scala._
import org.apache.flink.table.api.DataTypes
import org.apache.flink.table.api.scala._
import org.apache.flink.table.descriptors.{Csv, FileSystem, Schema}
import org.apache.flink.table.types.DataType
import org.apache.flink.types.Row

/**
 * @description:
 * @author: malichun
 * @time: 2020/9/16/0016 14:20
 *
 */
object FileOutputTest {
    def main(args: Array[String]): Unit = {
        //1.创建环境
        val env = StreamExecutionEnvironment.getExecutionEnvironment
        env.setParallelism(1)
        val tableEnv = StreamTableEnvironment.create(env)


        //2.连接外部系统,读取数据,注册表
        val filePath = getClass.getResource("/sensor.txt").getPath

        tableEnv.connect(new FileSystem().path(filePath))
            .withFormat(new Csv())
            .withSchema(new Schema()
                .field("id", DataTypes.STRING())
                .field("timestamp", DataTypes.BIGINT())
                .field("temp", DataTypes.DOUBLE())
                //              .field("pt",DataTypes.TIMESTAMP(3)).proctime()
            ).createTemporaryTable("inputTable")

        //3.转换操作
        val sensorTable = tableEnv.from("inputTable")

        //3.1简单转换
        val resultTable = sensorTable
            .select('id, 'temp)
            .filter('id === "sensor_1")

        //3.2复杂转换
        val aggTable = sensorTable
            .groupBy('id) //基于id分组
            .select('id, 'id.count as 'count )

        //4.输出到文件
        //注册输出表
        val outputPath = "E:\\gitdir\\learn_projects\\myLearn\\Flink_WuShengran\\src\\main\\resources\\output.txt"

        tableEnv.connect(new FileSystem().path(outputPath))
            .withFormat(new Csv())
            .withSchema(new Schema()
                .field("id", DataTypes.STRING())
                .field("temperature", DataTypes.DOUBLE())
//                .field("cnt", DataTypes.BIGINT())
            )
            .createTemporaryTable("outputTable")

        //输出到文件,直接会生成文件,不是output.txt文件夹!
        resultTable.insertInto("outputTable")

        resultTable.toAppendStream[(String, Double)].print("result")

        //group后要用toRetractStream方法
        aggTable.toRetractStream[Row].print("agg")

        env.execute()


    }
}
