package com.vlion.test.state.nostate

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
 * Created by John.Ma on 2021/8/9 0009 22:55
 */
object WordCount {

    def main(args: Array[String]): Unit = {
        //1. 初始化Spark配置信息
        val sparkConf = new SparkConf().setMaster("local[*]").setAppName("streamwordcount")
        // 2.初始化SparkStreamContext
        val ssc = new StreamingContext(sparkConf,Seconds(3))

        ssc.socketTextStream("www.bigdata01.com",7777)
            .flatMap(_.split(" "))
            .map((_,1))
            .reduceByKey(_+_)
            .print()

        ssc.start()
        ssc.awaitTermination()

    }
}
