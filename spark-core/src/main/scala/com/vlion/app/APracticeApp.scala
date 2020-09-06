package com.vlion.app

import com.vlion.bean.UserVisitAction
import com.vlion.handler.CategoryTop10Handler
import org.apache.log4j.Category
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object APracticeApp {
    def main(args: Array[String]): Unit = {
        val conf = new SparkConf().setAppName("Practice").setMaster("local[2]")

        val sc = new SparkContext(conf)

        val lineRDD:RDD[String] = sc.textFile("")

        val userVisitActionRDD = lineRDD.map(line => {
            val splits: Array[String] = line.split("_")
            UserVisitAction(
                splits(0),
                splits(1).toLong,
                splits(2),
                splits(3).toLong,
                splits(4),
                splits(5),
                splits(6).toLong,
                splits(7).toLong,
                splits(8),
                splits(9),
                splits(10),
                splits(11),
                splits(12).toLong)
        })

        //需求1:分组统计前10,每个品类点击的次数, 下单的次数和支付的次数.
        val infoes = CategoryTop10Handler.statCategoryTop10(sc, userVisitActionRDD)

        //需求2:
        // 需求 2: Top10热门品类中每个品类的 Top10 活跃 Session 统计

        // 对于排名前 10 的品类，分别获取每个品类点击次数排名前 10 的 sessionId。(注意: 这里我们只关注点击次数, 不关心下单和支付次数)
        //这个就是说，对于 top10 的品类，每一个都要获取对它点击次数排名前 10 的 sessionId。




    }
}
