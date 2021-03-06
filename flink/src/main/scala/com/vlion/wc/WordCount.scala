package com.vlion.wc


import org.apache.flink.api.scala._
import org.apache.flink.api.scala.ExecutionEnvironment


object WordCount {
    def main(args: Array[String]): Unit = {

        //创建执行环境
        val env = ExecutionEnvironment.getExecutionEnvironment

        // 从文件读取数据

        val inputDS = env.readTextFile("E:\\gitdir\\learn_projects\\myLearn\\flink\\src\\main\\resources\\test.txt")

        val wordCountDS = inputDS.flatMap(_.split("\\W+")).map((_, 1)).groupBy(0).sum(1)

        //打印输出
        wordCountDS.print()
    }
}
