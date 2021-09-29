package com.vlion.test.state.withstate

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
 * Created by John.Ma on 2021/8/9 0009 23:21
 */
object UpdateStateByKeyTest {
    def main(args: Array[String]): Unit = {
        // 定义更新方法
        val updateFunc = (values:Seq[Int], state:Option[Int]) => {
            val currentCount = values.sum
            val previousCount = state.getOrElse(0)
            Some(currentCount + previousCount)
        }


        //1. 初始化Spark配置信息
        val sparkConf = new SparkConf().setMaster("local[*]").setAppName("streamwordcount")
        // 2.初始化SparkStreamContext
        val ssc = new StreamingContext(sparkConf,Seconds(3))
        ssc.checkpoint("./ck")

        ssc.socketTextStream("www.bigdata01.com",7777)
            .flatMap(_ split " " )
            .map((_,1))
            .updateStateByKey(
                (seq:Seq[Int],options:Option[Int]) => {
                    Some( (0 /: seq)(_+_) + options.getOrElse(0))
                }
            )
            .print()

        ssc.start()
        ssc.awaitTermination()






    }

}
