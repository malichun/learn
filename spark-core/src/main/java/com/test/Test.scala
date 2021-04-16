package com.test

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @description:
 * @author: malichun
 * @time: 2021/2/25/0025 14:39
 *
 */
object Test {
    def main(args: Array[String]): Unit = {

        val sparkConf = new SparkConf().setMaster("local[*]").setAppName("test")
        val sc = new SparkContext(sparkConf)

        val rdd = sc.parallelize(1 to 100)
        val rdd2 = sc.parallelize(1 to 50)
        val a = (rdd intersection rdd2 ).collect
        a.sortBy(x => x).foreach(println)

        val list = List(1,2,3,4)
        list.flatMap(0 to _).reduce(_-_)


    }



    def convertRDD(rdd:RDD[String])={
        rdd.mapPartitions(iter => iter.map( line => {
            val arr = line.split("\\001")
            (arr(0),arr(1))
        }))

    }
}
