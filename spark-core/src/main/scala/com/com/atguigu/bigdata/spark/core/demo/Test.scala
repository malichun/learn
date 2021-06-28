package com.com.atguigu.bigdata.spark.core.demo

import org.apache.spark.{Partitioner, SparkConf, SparkContext}

import scala.collection.immutable.Iterable
import scala.collection.{Iterable, Iterator}

/**
 * timestamp	province	city	userid	adid
 *
 * 需求:
 * 日志文本 格式
 * timestamp       province  city      userid     adid
 * 某个时间点       某个省份    某个城市   用户id      广告id
 * 样例  151609240717   2          8         4          18
 *
 * 需求1.:统计每一个省份点击TOP3的广告
 * 分析:
 * 省份,每个广告    key    点击量  value
 * (省份、广告      广告点击量)
 * 1，2，  45
 * 1，3，  50
 * 1，4，  55
 *
 * 步骤：
 *    1.每个省份  每个广告  总的点击量（排序）
 *
 *    2.（省份_广告，点击量）
 *
 *
 */
case class AdClick(timestamp: Long,
                   province: Int,
                   city: Int,
                   uid: Int,
                   adid: Int
                  )
object Test {
    def main(args: Array[String]): Unit = {
        val sparkConf = new SparkConf().setAppName("log_process").setMaster("local[*]")
        val sc = new SparkContext(sparkConf)

        val rdd = sc.makeRDD(0 to 10,1).zipWithIndex()
        val r1 = rdd.mapPartitionsWithIndex((index,items) =>{
            Iterator(index+":【"+items.mkString(",")+"】")
        }).collect()

        println("默认分区..............")
        for(i <- r1){
            println(i)
        }
        println("默认分区..............")

        val rdd2 = rdd.partitionBy(new CustomPartition(5))
        val r2 = rdd2.mapPartitionsWithIndex((index,items) =>{
            Iterator(index+":【"+items.mkString(",")+"】")
        }).collect()
        r2.foreach(println)

        sc.stop()

    }



    //统计每一个省份点击TOP3的广告
    def xuqiu1(sc:SparkContext):Unit={
//        timestamp       province  city      userid     adid
//        某个时间点       某个省份    某个城市   用户id      广告id
//        样例  151609240717   2          8         4          18
        val adClikRDD = sc.textFile("").map(line => {
            val arr = line.split("\t")
            AdClick(arr(0).toLong, arr(1).toInt,arr(2).toInt,arr(3).toInt,arr(4).toInt)
        })

        val value = adClikRDD.mapPartitions(iter => {
            iter.map(adClick => ((adClick.province, adClick.adid), 1)) // 每一个省份的广告出现次数, 类似 group by prov, adid
        }).reduceByKey(_ + _)
            .map(t => (t._1._1, (t._1._2, t._2))) // (省份 , (广告id,count))
            .groupByKey()

        val value1 = value.map(t => {
            val provId = t._1
            val iter = t._2
            val topAds = t._2.toList.sortWith(_._2 < _._2).take(3)
            (provId, topAds)
        }).collect


    }

}


//自定义分区
class CustomPartition(numPartition: Int) extends Partitioner{
    override def numPartitions: Int = {
        numPartition
    }

    override def getPartition(key: Any): Int = {
        key.toString.toInt % numPartition // hashPartition
    }
}