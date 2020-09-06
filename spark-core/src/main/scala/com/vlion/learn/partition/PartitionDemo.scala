package com.vlion.learn.partition

import com.vlion.TEngine
import org.apache.spark.rdd.RDD
import org.apache.spark.{HashPartitioner, SparkConf, SparkContext}

object PartitionDemo extends App with TEngine {
    start("spark"){
        val sc = env.asInstanceOf[SparkContext]
        val rdd1=sc.parallelize(Array((10, "a"), (20, "b"), (30, "c"), (40, "d"), (50, "e"), (60, "f")))
        //把分区号取出来,检查元素的分区情况
        val rdd2 = rdd1.mapPartitionsWithIndex((index,iter) => iter.map(x => (index,x._1 + ":" + x._2)))
        println(rdd2.collect.mkString(","))

        //把RDD1 使用HashPartitioner 重新分区
        val rdd3 = rdd1.partitionBy(new HashPartitioner(5))

        //检测RDD3的分区情况
        val rdd4: RDD[(Int, String)] = rdd3.mapPartitionsWithIndex((index, it) => it.map(x => (index, x._1 + " : " + x._2)))
        println(rdd4.collect.mkString(","))

    }

}
