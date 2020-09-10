package com.atguigu.wc

import org.apache.flink.api.scala._

/**
 * Created by John.Ma on 2020/9/10 0010 22:19
 * 1.批处理的word count
 */
object WordCount {
    def main(args: Array[String]): Unit = {
        //创建一个批处理的执行环境
        val env: ExecutionEnvironment = ExecutionEnvironment.getExecutionEnvironment

        //从文件中读取数据
        val inputPath = "D:\\fileImportant\\Learn_projects\\learn\\Flink_WuShengran\\src\\main\\resources\\hello.txt"
        val inputDataSet = env.readTextFile(inputPath)

        //对数据进行转换处理统计,先分词,再按照word进行分组,最后进行聚合统计
        val resultDataSet: DataSet[(String, Int)] = inputDataSet
            .flatMap(_.split(" "))
            .map((_, 1))
            .groupBy(0) //以第一个元组作为key进行分组
            .sum(1)

        //打印输出
        resultDataSet.print()

    }
}
