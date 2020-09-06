package com.vlion.udf

import org.apache.spark.sql.{Row, SparkSession}
import org.apache.spark.sql.expressions.{MutableAggregationBuffer, UserDefinedAggregateFunction}
import org.apache.spark.sql.types.{DataType, DoubleType, LongType, StructField, StructType}

object UDFDemo01 {
    def main(args: Array[String]): Unit = {

        val spark: SparkSession = SparkSession.builder().master("local[*]")
            .appName("UDFDemo01")
            .getOrCreate()
        //注册自定义函数
        spark.udf.register("myAvg", new MyAvg)

        val df = spark.read.json("file://" + ClassLoader.getSystemResource("user.json").getPath)
        df.createTempView("user")
        spark.sql("select myAvg(age) age_avg from user").show
    }
}

class MyAvg extends UserDefinedAggregateFunction {
    /**
     * 返回聚合函数输入参数聚合类型
     *
     * @return
     */
    override def inputSchema: StructType = StructType(StructField("inputColumn", DoubleType) :: Nil)

    /**
     * 聚合缓冲区中值的类型
     *
     * @return
     */
    override def bufferSchema: StructType = StructType(StructField("sum", DoubleType) :: StructField("count", LongType) :: Nil)

    /**
     * 最终返回值类型
     * @return
     */
    override def dataType: DataType = DoubleType

    /**
     * 确定性: 比如同样的输入是否返回同样的输出
     * @return
     */
    override def deterministic: Boolean = true

    /**
     * 初始化
     * @param buffer
     */
    override def initialize(buffer: MutableAggregationBuffer): Unit = {
        //存数据的总和
        buffer(0) =0d
        //存储数据的个数
        buffer(1) = 0L

    }

    /**
     * 相同Executor间的合并
     * @param buffer
     * @param input
     */
    override def update(buffer: MutableAggregationBuffer, input: Row): Unit = {
        if(!input.isNullAt(0)){
            buffer(0) = buffer.getDouble(0) + input.getDouble(0)
            buffer(1) = buffer.getLong(1) + 1
        }

    }

    /**
     * 不同Executor间的合并
     * @param buffer1
     * @param buffer2
     */
    override def merge(buffer1: MutableAggregationBuffer, buffer2: Row): Unit = {
        if(!buffer2.isNullAt(0)){
            buffer1(0) = buffer1.getDouble(0) + buffer2.getDouble(0)
            buffer1(1) = buffer1.getLong(1) + buffer2.getLong(1)
        }
    }

    /**
     * 计算最终结果,因为是聚合函数,所以最后只有一行了
     * @param buffer
     * @return
     */
    override def evaluate(buffer: Row): Double = {
        println(buffer.getDouble(0),buffer.getLong(1))
        buffer.getDouble(0)/buffer.getLong(1)
    }
}
