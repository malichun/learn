package com.vlion.learn.partition

import com.vlion.TEngine
import org.apache.spark.rdd.RDD
import org.apache.spark.{Partitioner, SparkContext}

import scala.collection.mutable


/*
使用自定义的 Partitioner 是很容易的 :只要把它传给 partitionBy() 方法即可。

Spark 中有许多依赖于数据混洗的方法，比如 join() 和 groupByKey()，
它们也可以接收一个可选的 Partitioner 对象来控制输出数据的分区方式。
*/
object MyPartitinoerDemo extends App with TEngine{
//    start(){
//        val sc = env.asInstanceOf[SparkContext]
//        val rdd1 = sc.parallelize(
//            Array((10, "a"), (20, "b"), (30, "c"), (40, "d"), (50, "e"), (60, "f")), 3)
//
//        val rdd2 = rdd1.partitionBy(new MyPartitioner(4))
//
//        val rdd3: RDD[(Int, String)] = rdd2.mapPartitionsWithIndex((index, items) => items.map(x => (index, x._1 + " : " + x._2)))
//        println(rdd3.collect.mkString(" "))
//
//    }

    val map1 = mutable.Map[String,Int](("a",1),("b",2),("c",3))
    val map2 = mutable.Map[String,Int](("b",2),("c",10),("d",1),("a",15))
    //def foldLeft[B](z: B)(op: (B, A) => B): B = {
    val m=map1.foldLeft(map2)((map,kv) => {
        val m=map.updated(kv._1,kv._2+map.getOrElse(kv._1,0))
        m
    })

    println(map2)
    println(m)


}

class MyPartitioner(numPars:Int) extends Partitioner{

    override def numPartitions: Int = numPars

    override def getPartition(key: Any): Int =1

}
