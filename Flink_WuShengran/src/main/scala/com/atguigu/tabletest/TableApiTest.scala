package com.atguigu.tabletest

import org.apache.flink.api.scala.ExecutionEnvironment
import org.apache.flink.streaming.api.scala._
import org.apache.flink.table.api.{EnvironmentSettings, TableEnvironment}
import org.apache.flink.table.api.scala.{BatchTableEnvironment, StreamTableEnvironment}

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



        //1.1 基于老版本planner的流处理
        val settings = EnvironmentSettings.newInstance()
            .useOldPlanner()
            .inStreamingMode()
            .build()

        val oldStreamTableEnv = StreamTableEnvironment.create(env,settings)

        //1.2基于老版本的批处理
        val batchEnv = ExecutionEnvironment.getExecutionEnvironment
        val oldBatchTableEnv = BatchTableEnvironment.create(batchEnv)

        //1.3基于blink planner的流处理
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


    }
}
