package com.vlion.ss.join

import org.apache.spark.sql.{DataFrame, SparkSession}

object StreamingStaticJoins {
    //模拟的静态数据:
    //lisi,male
    //zhiling,female
    //zs,male
    //模拟的流式数据:
    //lisi,20
    //zhiling,40
    //ww,30
    def main(args: Array[String]): Unit = {
        val spark:SparkSession = SparkSession.builder()
            .master("local[*]")
            .appName("StreamingStatic")
            .getOrCreate()
        import spark.implicits._

        //1.静态df
        val arr = Array(("lisi", "male"), ("zhiling", "female"), ("zs", "male"))

        var staticDF:DataFrame = spark.sparkContext.makeRDD(arr).toDF("name","sex")

        //2.流式DF
        val lines:DataFrame = spark.readStream
            .format("socket")
            .option("host","www.bigdata05.com")
            .option("port",4444)
            .load()

        val streamDF:DataFrame = lines.as[String]
            .map(line => {
                val arr = line.split(",")
                (arr(0),arr(1).toInt)
            }).toDF("name","age")

        //3.join
        val joinResult:DataFrame = streamDF.join(staticDF,"name")

        //4.输出
        joinResult.writeStream
            .outputMode("append")
            .format("console")
            .start()
            .awaitTermination()

    }
}
