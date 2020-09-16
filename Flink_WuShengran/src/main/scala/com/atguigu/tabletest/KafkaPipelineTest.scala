package com.atguigu.tabletest

import org.apache.flink.streaming.api.scala._
import org.apache.flink.table.api.DataTypes
import org.apache.flink.table.api.scala._
import org.apache.flink.table.descriptors.{Csv, Kafka, Schema}

/**
 * @description:
 * @author: malichun
 * @time: 2020/9/16/0016 14:58
 *
 */
object KafkaPipelineTest {
    def main(args: Array[String]): Unit = {
        //1.创建环境
        val env = StreamExecutionEnvironment.getExecutionEnvironment
        env.setParallelism(1)

        val tableEnv = StreamTableEnvironment.create(env)

        //2.从kafka读取数据
        tableEnv.connect(new Kafka()
            .version("0.11")
            .topic("ad_log")
            .property("zookeeper.connect", "www.bigdata03.com:2181,www.bigdata02.com:2181,www.bigdata04.com:2181")
            .property("bootstrap.servers", "www.bigdata04.com:9092,www.bigdata05.com:9092,www.bigdata06.com:9092")
        )
            .withFormat(new Csv())
            .withSchema(new Schema()
                .field("id", DataTypes.STRING)
                .field("timestamp", DataTypes.BIGINT())
                .field("temperature", DataTypes.DOUBLE())
            )
            .createTemporaryTable("kafkaInputTable")

        //3.查询转换
        //3.1简单转换
        val sensorTable = tableEnv.from("kafkaInputTable")
        val resultTable = sensorTable
            .select('id, 'temperature)
            .filter('id === "sensor_1")

        //3.1聚合转换
        val aggTable = sensorTable
            .groupBy('id) //基于id分组
            .select('id, 'id.count as 'count)

        //4.输出到kafka
        tableEnv.connect(new Kafka()
            .version("0.11")
            .topic("ads_log")
            .property("zookeeper.connect", "www.bigdata03.com:2181,www.bigdata02.com:2181,www.bigdata04.com:2181")
            .property("bootstrap.servers", "www.bigdata04.com:9092,www.bigdata05.com:9092,www.bigdata06.com:9092")
        )
            .withFormat(new Csv())
            .withSchema(new Schema()
                .field("id", DataTypes.STRING())
                .field("temp", DataTypes.DOUBLE())
            )
            .createTemporaryTable("kafkaOutputTable")

        resultTable.insertInto("kafkaOutputTable")

        env.execute("kafka pipeline test")

    }
}
