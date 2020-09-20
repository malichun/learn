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

        tableEnv.connect(new FileSystem().path("E:\\gitdir\\learn_projects\\myLearn\\Flink_WuShengran\\src\\main\\resources\\sensor.txt"))
            .withFormat(new Csv())
            .withSchema(new Schema()
                .field("id", DataTypes.STRING())
                .field("timestamp", DataTypes.BIGINT())
                .field("temp", DataTypes.DOUBLE())
            ).createTemporaryTable("inputTable")

        //3.转换操作
        val sensorTable = tableEnv.from("inputTable")
        //3.1简单转换
        val resultTable = sensorTable
            .select('id, 'temp)
            .filter('id === "sensor_1")

        // 3.2聚合转换
        val aggTable = sensorTable
            .groupBy('id) //基于id分组
            .select('id, 'id.count as 'count)


        // 4.输出到文件
        val outputPath ="E:\\gitdir\\learn_projects\\myLearn\\Flink_WuShengran\\src\\main\\resources\\out.txt"
        tableEnv.connect(new FileSystem().path(outputPath))
            .withFormat(new Csv)
            .withSchema(new Schema()
                .field("id1", DataTypes.STRING())
                .field("temperature1", DataTypes.DOUBLE())  //输出表的字段名字可以和insert表的名字的字段不一样
            ).createTemporaryTable("outputTable")

        //        resultTable.toAppendStream[(String,Double)].print("result")
        //        aggTable.toRetractStream[(String,Long)].print("agg result") //返回带一个boolean

        resultTable.insertInto("outputTable")
//        aggTable.insertInto("")  //不行,聚合之后的函数不行,更改操作不行 AppendStreamTableSink
        env.execute("file output test")

    }
}
