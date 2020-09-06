package com.vlion.realtime.mock

import java.util.Properties

import com.vlion.realtime.CityInfo
import com.vlion.realtime.util.{RandomNumUtil, RandomOptions}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

import scala.collection.mutable.ArrayBuffer

/**
 * 数据格式
 * timestamp area city userid adid
 * 某个时间点    地区 城市 用户  广告id
 */
object MockRealTime {

    def mockRealTimeData():ArrayBuffer[String] = {
        val array = ArrayBuffer[String]()
        //城市信息
        val randomOpts = RandomOptions(
            (CityInfo(1, "北京", "华北"), 30),
            (CityInfo(2, "上海", "华东"), 30),
            (CityInfo(3, "广州", "华南"), 10),
            (CityInfo(4, "深圳", "华南"), 20),
            (CityInfo(4, "杭州", "华中"), 10)
        )
        (1 to 50).foreach {
            i => {
                val timestamp = System.currentTimeMillis()
                val cityInfo = randomOpts.getRandomOption()
                val area = cityInfo.area
                val city = cityInfo.cityName
                val userid = RandomNumUtil.randomInt(100, 105)
                val adid = RandomNumUtil.randomInt(1, 5)
                array += s"$timestamp,$area,$city,$userid,$adid"
                Thread.sleep(10)
            }
        }
        array
    }

    def createKafkaProducer: KafkaProducer[String, String] = {
        val props: Properties = new Properties
        // Kafka服务端的主机名和端口号
        props.put("bootstrap.servers", "www.bigdata04.com:9092,www.bigdata05.com:9092,www.bigdata06.com:9092")
        // key序列化
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
        // value序列化
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
        new KafkaProducer[String, String](props)
    }

    def main(args: Array[String]): Unit = {
        val topic = "ads_log"
        val producer: KafkaProducer[String, String] = createKafkaProducer
        while (true) {
            mockRealTimeData().foreach {
                msg => {
                    producer.send(new ProducerRecord(topic, msg))
                    Thread.sleep(100)
                }
            }
            Thread.sleep(1000)
        }
    }

}
