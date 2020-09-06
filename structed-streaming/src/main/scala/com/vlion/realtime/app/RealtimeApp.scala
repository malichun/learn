package com.vlion.realtime.app

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date

import com.vlion.realtime.bean.AdsInfo
import org.apache.spark.sql.{Dataset, SparkSession}

/**
 * 日志格式:1597656506953,华东,上海,104,3
 */
object RealtimeApp {
    def main(args: Array[String]): Unit = {

        val spark:SparkSession = SparkSession
                .builder()
            .master("local[2]")
            .appName("RealtimeApp")
            .getOrCreate()

        spark.sparkContext.setLogLevel("WARN")
        import spark.implicits._

        val dayStringFormatter:SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd")
        val hmStringFormatter:SimpleDateFormat = new SimpleDateFormat("HH:mm")

        //1.从kafka 读取数据,为了方便后续处理,封装数据到AdsInfo 样例类中
        val adsInfoDS:Dataset[AdsInfo] = spark.readStream
            .format("kafka")
            .option("kafka.bootstrap.servers","www.bigdata04.com:9092,www.bigdata05.com:9092,www.bigdata06.com:9092")
            .option("subscribe","ads_log")
            .load
            .select("value")
            .as[String]
            .map(v => {
                val split:Array[String] = v.split(",")
                val date:Date = new Date(split(0).toLong)
                AdsInfo(split(0).toLong,
                    new Timestamp(split(0).toLong),
                    dayStringFormatter.format(date),
                    hmStringFormatter.format(date),
                    split(1),
                    split(2),
                    split(3),
                    split(4)
                )
            })

        adsInfoDS.writeStream
            .format("console")
            .outputMode("update")
            .option("truncate","false")
            .start()
            .awaitTermination()


    }
}
