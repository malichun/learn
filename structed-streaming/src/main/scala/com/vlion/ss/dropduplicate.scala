package com.vlion.ss

import java.sql.Timestamp

import org.apache.spark.sql.{Dataset, Row}

//1,2019-09-14 11:50:00,dog
//2,2019-09-14 11:51:00,dog
//1,2019-09-14 11:50:00,dog
//3,2019-09-14 11:53:00,dog
//1,2019-09-14 11:50:00,dog
//4,2019-09-14 11:45:00,dog

import org.apache.spark.sql.{DataFrame, SparkSession}

object dropduplicate {
    def main(args: Array[String]): Unit = {
        val spark:SparkSession = SparkSession
            .builder()
            .master("local[*]")
            .appName("Test")
            .getOrCreate()


        import spark.implicits._

        val lines:DataFrame = spark.readStream
            .format("socket")
            .option("host","www.bigdata05.com")
            .option("port",4444)
            .load()

        val words:DataFrame = lines.as[String].map(line => {
            val arr:Array[String] = line.split(",")
            (arr(0),Timestamp.valueOf(arr(1)),arr(2))
        }).toDF("uid","ts","word")

        val wordCounts:Dataset[Row] = words
            .withWatermark("ts","2 minutes")
            .dropDuplicates("uid") //去重 重复数据 uid相同就是重复,可以传递多个列

        wordCounts.writeStream
            .outputMode("append")
            .format("console")
            .start()
            .awaitTermination()

    }
}
