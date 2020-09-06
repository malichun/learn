package com.vlion.ad_project

import java.sql.Connection

import com.vlion.ad_project.handler.{BlackListHandler, DateAreaCityAdCountHandler, LastHourAdCountHandler}
import com.vlion.ad_project.model.Ads_log
import com.vlion.ad_project.util.{JdbcUtil, MyKafkaUtil, PropertiesUtil}
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
 * 7.5 需求三：最近一小时广告点击量
 * 结果展示：
 * 1：List[15:50->10,15:51->25,15->52->30]
 * 2：List [15:50->10,15:51->25,15->52->30]
 * 3：List [15:50->10,15:51->25,15->52->30]
 * 7.5.1 思路分析
 * 1）开窗确定时间范围；
 * 2）在窗口内将数据转换数据结构为((adid,hm),count);
 * 3）按照广告id进行分组处理，组内按照时分排序。
 */
object RealTimeApp3 {
    def main(args: Array[String]): Unit = {
        //1.创建SparkConf
        val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("RealTimeApp")
        //2.创建StreamingContext
        val ssc = new StreamingContext(sparkConf, Seconds(3))

        //3.读取Kafka数据  1583288137305 华南 深圳 4 3
        val topic: String = PropertiesUtil.load("config.properties").getProperty("kafka.topic")
        val kafkaDStream: InputDStream[ConsumerRecord[String, String]] = MyKafkaUtil.getKafkaStream(topic, ssc)

        //4.将每一行数据转换为样例类对象
        val adsLogDStream: DStream[Ads_log] = kafkaDStream.map(record => {
            //a.取出value并按照" "切分
            val arr: Array[String] = record.value().split(" ")
            //b.封装为样例类对象
            Ads_log(arr(0).toLong, arr(1), arr(2), arr(3), arr(4))
        })


        //5.根据Mysql中的黑名单表进行数据过滤
        val filterAdsLogDStream:DStream[Ads_log] = adsLogDStream.filter(adsLog => {
            //查询mysql,查看当前用户是否存在
            val connection: Connection = JdbcUtil.getConnection
            val bool: Boolean = JdbcUtil.isExists(connection, "select * from black_list where userid=?", Array(adsLog.userid))
            connection.close()
            !bool
        })

        filterAdsLogDStream.cache()

        //6.对没有被加入黑名单的用户统计当前批次单日各个用户对各个广告点击的总次数,
        // 并更新至MySQL
        // 之后查询更新之后的数据,判断是否超过100次。
        // 如果超过则将给用户加入黑名单
        BlackListHandler.addBlackList(filterAdsLogDStream)

        //7.统计每天各大区各个城市广告点击总数并保存至MySQL中
        DateAreaCityAdCountHandler.saveDateAreaCityAdCountToMysql(filterAdsLogDStream)


        //8.统计最近一小时(2分钟)广告分时点击总数
        val adToHmCountListDStream:DStream[(String,List[(String,Long)])]=LastHourAdCountHandler.getAdHourMintToCount(filterAdsLogDStream)

        //9.打印
        adToHmCountListDStream.print()

        //10.开启任务
        ssc.start()
        ssc.awaitTermination()


    }
}
