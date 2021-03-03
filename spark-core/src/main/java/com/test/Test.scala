package com.test

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


    }
}
