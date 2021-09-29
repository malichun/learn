package com.vlion.test

import org.apache.spark.{Partitioner, SparkConf, SparkContext}
import org.apache.spark.sql.SparkSession
import org.apache.spark.util.AccumulatorV2

import scala.collection.mutable

/**
 * Created by John.Ma on 2021/7/31 0031 10:59
 * 分别统计每个品类点击的次数, 下单的次数和支付的次数
 */
object Test {
    def main(args: Array[String]): Unit = {
        val conf = new SparkConf().setAppName("spark").setMaster("local[*]")
        val sc = new SparkContext(conf)

        val rdd1 = sc.makeRDD(List(1, 2, 3, 4), 2)
        val rdd2 = sc.makeRDD(List(3, 4, 5, 6), 4)

        val rdd6 = rdd1.zip(rdd2)

        println(rdd6.collect().mkString)


        val rdd = sc.textFile("hdfs://www.bigdata02.com:8020/user/hive/warehouse/ods.db/ods_dsp_info/etl_date=2021-08-07/*/")

        rdd.flatMap(line => {
            val arr = line.split("\t")
            if(arr.length == 28 && arr(0) == "10"){
                List((arr(27),1))
            }else{
                List[(String,Int)]()
            }
        })
            .combineByKey((v: Int) => 1,
                (u:Int,v:Int)=> u+1,
                (c1:Int,c2:Int) => c1+c2
            ).collect.foreach(println)

        val spark = SparkSession.builder().getOrCreate()




        sc.stop()
    }
}

//
// 求平均数
//
class MapAccumulator extends AccumulatorV2[String, mutable.Map[(String), (Long, Long)]] {
    val map = mutable.Map[String, (Long, Long)]()

    override def isZero: Boolean = map.isEmpty

    // 拷贝一个新的累加器
    override def copy(): AccumulatorV2[String, mutable.Map[String, (Long, Long)]] = {
        val newAcc = new MapAccumulator()
        newAcc.synchronized {
            newAcc.map ++= map
        }
        newAcc
    }

    //重置一个累加器
    override def reset(): Unit = {
        map.clear()
    }

    // 每一个分区中用于添加数据的放
    override def add(v: String): Unit = {
        val tuple = map.getOrElse(v, (0L, 0L))

    }

    // 多个分区中永不添加数据的方法
    override def merge(other: AccumulatorV2[String, mutable.Map[String, (Long, Long)]]): Unit = ???

    // 最终输出
    override def value: mutable.Map[String, (Long, Long)] = ???
}