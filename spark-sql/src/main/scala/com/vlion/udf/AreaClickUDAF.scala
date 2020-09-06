package com.vlion.udf

import org.apache.spark.sql.Row
import org.apache.spark.sql.expressions.{MutableAggregationBuffer, UserDefinedAggregateFunction}
import org.apache.spark.sql.types.{DataType, DoubleType, LongType, MapType, StringType, StructField, StructType}

import scala.collection.mutable

/**
 * 这里的热门商品是从点击量的维度来看的.
 * 计算各个区域前三大热门商品，并备注上每个商品在主要城市中的分布比例，超过两个城市用其他显示。
 *
 * 地区	商品名称	点击次数	城市备注
 * 华北	商品A	100000	北京21.2%，天津13.2%，其他65.6%
 * 华北	商品P	80200	北京63.0%，太原10%，其他27.0%
 * 华北	商品M	40000	北京63.0%，太原10%，其他27.0%
 * 东北	商品J	92000	大连28%，辽宁17.0%，其他 55.0%
 *
 * select
 * area,
 * product_name,
 * count(1) as click_count,
 * city_remark(t1.city_name)
 * from
 * t1
 * group by t1.area,t1.product_name
 *
 * //主要:
 * city_remark(t1.city_name)
 *
 */
class AreaClickUDAF extends UserDefinedAggregateFunction {
    /**
     * 返回聚合函数输入参数聚合类型
     *
     * @return
     */
    override def inputSchema: StructType = StructType(StructField("city_name", StringType) :: Nil)

    /**
     * 聚合缓冲区中值的类型
     *
     * @return
     */
    override def bufferSchema: StructType = StructType(StructField("city_count", MapType(StringType, LongType)) :: Nil)

    /**
     * 最终返回值类型
     *
     * @return
     */
    override def dataType: DataType = StringType

    /**
     * 确定性: 比如同样的输入是否返回同样的输出
     *
     * @return
     */
    override def deterministic: Boolean = true

    /**
     * 初始化
     *
     * @param buffer
     */
    override def initialize(buffer: MutableAggregationBuffer): Unit = {
        buffer(0) = Map[String, Long]()

    }

    /**
     * 分区内Map[城市名,点击量]合并
     *
     * @param buffer
     * @param input
     */
    override def update(buffer: MutableAggregationBuffer, input: Row): Unit = {
        if (!input.isNullAt(0)) {
//            val mapBuffer = buffer.getMap[String, Long](0)
            val map = buffer.getAs[Map[String,Long]](0)
            val cityName = input.getString(0)  //外面的输入

            //两个map合并
            buffer(0)=map + (cityName -> (map.getOrElse(cityName,0L)+1L))
        }

    }

    /**
     * 不同Executor间的合并
     *
     * @param buffer1
     * @param buffer2
     */
    override def merge(buffer1: MutableAggregationBuffer, buffer2: Row): Unit = {
        if (!buffer2.isNullAt(0)) {
            val mapBuffer = buffer1.getMap[String, Long](0)
            val mapInput = buffer2.getMap[String, Long](0)

            val mapResult = mutable.Map(mapInput.toSeq: _*)
            //两个map合并
            buffer1(0)=mapBuffer.foldLeft(mapResult)((m, kv) => {
                m.update(kv._1, m.getOrElse(kv._1, 0L) + kv._2)
                m
            }
            )
        }
    }

    /**
     * 计算最终结果,因为是聚合函数,所以最后只有一行了
     *
     * @param buffer
     * @return
     */
    override def evaluate(buffer: Row): String = {
        val res = buffer.getMap[String, Long](0)
        val totalNum = res.values.sum
        val sortedRes = res.toList.sortBy(_._2)(Ordering.Long.reverse).take(2)
        //大连28%，辽宁17.0%，其他 55.0%
        var rate = 0.0
        val stringBuffer =new StringBuffer
        (stringBuffer /: sortedRes)((s,t) => {
            rate += t._2/totalNum
            s.append(t._1).append(t._2/totalNum).append(",")
        })
        stringBuffer.append("其他").append(1-rate)
        stringBuffer.toString
    }
}
