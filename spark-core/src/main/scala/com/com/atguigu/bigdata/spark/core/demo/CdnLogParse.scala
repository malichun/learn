package com.com.atguigu.bigdata.spark.core.demo

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

import scala.util.matching.Regex

/**
 * 日志解析
 * 日志格式：
 * IP 命中率 响应时间 请求时间 请求方法 请求URL    请求协议 状态吗 响应大小 referer 用户代理
 * ClientIP Hit/Miss ResponseTime [Time Zone] Method URL Protocol StatusCode TrafficSize Referer UserAgent
 *
 * 1.计算每一个IP的访问次数
 * 2.计算每一个视频访问的ip数
 * 3.统计每小时CDN流量
 */
object CdnLogParse {
    //匹配IP地址
    val IPPattern = "((?:(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d))))".r

    //匹配视频文件名
    val videoPattern = "([0-9]+).mp4".r

    //[15/Feb/2017:11:17:13 +0800]  匹配 2017:11 按每小时播放量统计
    val timePattern= ".*(2017):([0-9]{2}):[0-9]{2}:[0-9]{2}.*".r

    //匹配http响应码和请求数据大小
    val httpSizePattern = ".*\\s(200|206|304)\\s([0-9]+)\\s.*".r

    def main(args: Array[String]): Unit = {
        val sparkConf = new SparkConf().setAppName("cdnLogParse").setMaster("local[*]")

        val sc= new SparkContext(sparkConf)

        val input = sc.textFile("").cache()
    }

    /**
     * 1.计算每一个IP的访问次数
     */
    def ipStatis(data:RDD[String])={
        // 统计独立ip数
        val ipNums = data.map(x => (IPPattern.findFirstMatchIn(x).get,1))
            .reduceByKey(_+_)

        //输出IP访问数量的前10
        ipNums.take(10).foreach(println)
    }

    /**
     * 2.计算每一个视频访问的独立ip数
     */
    def videoIpStatistics(data:RDD[String])={
        def getFileNameAndIp(line:String): (String, String) ={ // (名称,ip)
            (videoPattern.findFirstMatchIn(line).mkString,IPPattern.findFirstMatchIn(line).mkString)
        }
        data.filter(x => x.matches(".*([0-9]+)\\.mp4.*"))
            .map(x => getFileNameAndIp(x))
            .distinct()
            .groupByKey()
            .sortBy(_._2.size,false)
            .take(10)
            .foreach(println)
    }

    /**
     * 3.统计每小时CDN流量
     */
    def flowOfHour(data:RDD[String]) = {
        def isMatch(pattern:Regex, str:String):Boolean ={
            str match{
                case pattern(_*) => true //转换成参数序列
                case _ => false
            }
        }

        def getTimeAndSize(line:String) = {
            var res = ("",0L)
            try{
               val httpSizePattern(code ,size) = line
                val timePattern(year,hour) = line
                res = (hour, size.toLong)

            }catch {
                case ex:Exception => ex.printStackTrace()
            }
            res
        }

    }


}
