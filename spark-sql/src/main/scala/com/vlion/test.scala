package com.vlion

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object test {
    def main(args: Array[String]): Unit = {
        val conf = new SparkConf().setMaster("local[*]")

        val sc = new SparkContext(conf)

        val rdd = sc.parallelize(List(("a",3),("a",2),("c",4),("b",3),("c",6),("c",8)),2)
        rdd.aggregateByKey((0,0))(
            (u:(Int,Int),v:Int) => (u._1+v,u._2+1),
                (u1:(Int,Int),u2:(Int,Int)) => (u1._1+u2._1,u1._2+u2._1)
        )
            .map(t => t._1+"平均值:"+(t._2._1/t._2._2.toDouble))
        rdd.zipWithIndex()

    }
}
