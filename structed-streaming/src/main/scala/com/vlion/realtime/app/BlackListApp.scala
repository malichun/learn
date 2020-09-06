package com.vlion.realtime.app

import com.vlion.realtime.bean.AdsInfo
import com.vlion.realtime.util.RedisUtil
import org.apache.spark.sql.streaming.Trigger
import org.apache.spark.sql.{DataFrame, Dataset, ForeachWriter, Row, SparkSession}
import redis.clients.jedis.Jedis

/**
 * 需求1: 统计黑名单
 */
object BlackListApp {
    def statBlackList(spark: SparkSession, adsInfoDS: Dataset[AdsInfo]): Dataset[AdsInfo] = {
        import spark.implicits._
        //1.过滤黑名单数据:如果有用户已经进入黑名单,则不再统计这个用户的广告点击记录
        val filteredAdsInfoDS: Dataset[AdsInfo] = adsInfoDS.mapPartitions(adInfoIt => {
            // 每个分区连接一次到redis读取黑名单, 然后把进入黑名单用户点击记录过滤掉
            val adsInfoList:List[AdsInfo] = adInfoIt.toList
            if(adsInfoList.isEmpty){
                adsInfoList.toIterator
            }else{
                //先读取到黑名单
                val client:Jedis = RedisUtil.getJedisClient

                val blackList:java.util.Set[String] = client.smembers(s"day:blcklist:${adsInfoList(0).dayString}")
                //2 .过滤
                adsInfoList.filter(adsInfo => {
                    !blackList.contains(adsInfo.userId)
                }).toIterator

            }
        })

        //创建临时表,tb_ads_info
        filteredAdsInfoDS.createOrReplaceTempView("tb_ads_info")

        //需求1:黑名单每天 每用户广告点击量

        //2.按照每天用户每id分组,然后计数,计数超过阈值(100)的查询出来
        val result:DataFrame = spark.sql(
            """
              |select
              | dayString,
              | userId
              |from tb_ads_info
              |group by dayString,userId,adsId
              |having count(1) > 100000
              |
              |""".stripMargin)

        //3.把点击量超过100的写入到redis中
        result.writeStream
            .outputMode("update")
            .trigger(Trigger.ProcessingTime("2 seconds"))
            .foreach(new ForeachWriter[Row] {
                var client:Jedis = _
                override def open(partitionId: Long, epochId: Long): Boolean = {
                    //打开redis的连接
                    client = RedisUtil.getJedisClient
                    client!=null
                }

                override def process(value: Row): Unit = {
                    //写入到redis 把每天的黑名单写入到set中, key:"day:blacklist" value:黑名单用户
                    val dayString = value.getString(0)
                    val userId = value.getString(1)
                    client.sadd(s"day:blacklist:$dayString",userId)

                }

                override def close(errorOrNull: Throwable): Unit = {
                    //关闭到redis的连接
                    if(client != null ) client.close()
                }
            }).option("checkpointLoaction","C:/blacklist")
            .start


        //4.把过滤后数据返回 (在其他地方也可以使用临时表 tb_ads_info)
        filteredAdsInfoDS

    }


}
