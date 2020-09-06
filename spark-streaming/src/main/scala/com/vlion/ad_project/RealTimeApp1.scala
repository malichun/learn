package com.vlion.ad_project

import com.vlion.ad_project.handler.BlackListHandler
import com.vlion.ad_project.model.Ads_log
import com.vlion.ad_project.util.MyKafkaUtil
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
 * 需求一:广告黑名单
 *
 * 实现实时的动态黑名单机制：将每天对某个广告点击超过 100 次的用户拉黑。
 * 注：黑名单保存到MySQL中。
 *
 * 7.3.1 思路分析
 * 1）读取Kafka数据之后，并对MySQL中存储的黑名单数据做校验；
 * 2）校验通过则对给用户点击广告次数累加一并存入MySQL；
 * 3）在存入MySQL之后对数据做校验，如果单日超过100次则将该用户加入黑名单。
 */
object RealTimeApp1 {
    def main(args: Array[String]): Unit = {
        //1.创建SparkConf
        val sparkConf:SparkConf = new SparkConf().setAppName("RealTimeApp").setMaster("local[*]")

        //2.创建StreamingContext
        val ssc = new StreamingContext(sparkConf,Seconds(3))

        //3.读取数据
        val kafkaDStream:InputDStream[ConsumerRecord[String,String]]= MyKafkaUtil.getKafkaStream("first",ssc)

        //4.将从Kafka度去除的数据转换为样例类
        val adsLogDStream:DStream[Ads_log] = kafkaDStream.map(record => {
            val value = record.value()
            val arr = value.split(" ")
            //时间戳   区域   城市   userid   adid
            Ads_log(arr(0).toLong,arr(1),arr(2),arr(3),arr(4))
        })

        //5.需求一:根据MySQL中的黑名单过滤当前数据集
        val filterAdsLogDStream:DStream[Ads_log]=BlackListHandler.filterByBlackList(adsLogDStream) //过滤出已经被拉黑名单的用户,意思就是剩下的没有被拉黑的

//        filterAdsLogDStream.print()

        //6.需求一:将满足要求的用户写入黑名单
        BlackListHandler.addBlackList(filterAdsLogDStream)

        //7.测试打印
        filterAdsLogDStream.cache()
        filterAdsLogDStream.count().print()

        //启动任务
        ssc.start()
        ssc.awaitTermination()

    }
}
