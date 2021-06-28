package com.com.atguigu.bigdata.spark.core.demo

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.util.AccumulatorV2

import scala.collection.mutable

/**
 * @description:
 * @author: malichun
 * @time: 2021/6/11/0011 15:44
 *
 */
object ACCTest {
    def main(args: Array[String]): Unit = {
        val sparkConf = new SparkConf().setAppName("accumulator").setMaster("local[*]")
        val sc = new SparkContext(sparkConf)

        val hashAcc = new CustomACC
        //注册累加器
        sc.register(hashAcc,"abc")

        val rdd = sc.makeRDD(Array("a","b","c","a","b","c","d"))

        rdd.foreach(hashAcc.add) // 累加器最好在action中使用// 统计出现次数

        hashAcc.value.foreach(println)


        sc.stop()
    }
}

/**
 * 实现自定义累加器
 */
class CustomACC extends AccumulatorV2[String, mutable.HashMap[String,Int]]{ // In / Out

    private val _hashACC = new mutable.HashMap[String,Int]()

    // 检测是否为空
    override def isZero: Boolean = {
        _hashACC.isEmpty
    }

    // 拷贝一个新的累加器
    override def copy(): AccumulatorV2[String, mutable.HashMap[String, Int]] = {
        val newAcc = new CustomACC
        _hashACC.synchronized{
            newAcc._hashACC ++= _hashACC
        }
        newAcc
    }

    // 重置一个累加器
    override def reset(): Unit = {
        _hashACC.clear()
    }

    // 每一个分区中用于添加数据的方法 => 小的sum
    override def add(v: String): Unit = {
        _hashACC.get(v) match{
            case None => _hashACC += ((v,1))
            case Some(a) => _hashACC += ((v,a+1))
        }
    }

    // 合并每一个分区的输出;总的sum
    override def merge(other: AccumulatorV2[String, mutable.HashMap[String, Int]]): Unit = {
        // 当前_hashACC作为0值,目的更新当前_hashAcc的值
        (_hashACC /: other.value){case (currentAcc: mutable.Map[String, Int],(key: String,count: Int)) =>
            currentAcc.get(key) match {
                case None => currentAcc += ((key,count))
                case Some(a) => currentAcc += ((key,count+a))
            }
        }

//        other match{
//            case o:AccumulatorV2[String,mutable.HashMap[String,Int]] => {
//                for((k,v) <- o.value){
//                    _hashACC.get(k) match{
//                        case None => _hashACC += ((k,v))
//                        case Some(a) => _hashACC += ((k,a+v))
//                    }
//                }
//            }
//        }
    }

    //输出值
    override def value: mutable.HashMap[String, Int] = {
        _hashACC
    }
}
