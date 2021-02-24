package com.vlion

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @description:
 * @author: malichun
 * @time: 2020/11/19/0019 10:38
 *
 */
case class AdsLog(timestamp: String, province: String, city: String, user: String, adId: String)

object Demo_top3 {
    def main(args: Array[String]): Unit = {
        //1.初始化Spark配置
        val conf = new SparkConf().setMaster("local[*]").setAppName("adClkTop3")
        val sc = new SparkContext(conf)

        val rdd = sc.textFile("/tmp/test/agent.log")

        //需求:统计每一个省份广告被点击次数的top3
        //时间戳  省份  城市  用户  广告

        val logRdd = rdd.mapPartitions(iter => {
            iter.filter( line => !line.isEmpty).map(_.split(" ")).collect{case Array(t,p,c,u,a) => AdsLog(t,p,c,u,a)}
        })

        val value = logRdd.map(a => ((a.province, a.adId), 1))
            .reduceByKey(_ + _)
            .map {
                case ((province, adId), count) =>
                    (province, (adId, count))
            }
                .groupByKey()
                .mapValues(iter=>{
                    iter.iterator.toList.sortBy(_._2)(Ordering.Int.reverse).take(3)
                })

        value.collect

        Thread.sleep(2000000)
        sc.stop

    }
}
