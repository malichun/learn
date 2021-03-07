package com.atguigu.summer.core

import java.util.Properties

import com.atguigu.summer.utils.{EnvUtil, ProUtils, PropertiesUtil}
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}

/**
 * @description:
 * @author: malichun
 * @time: 2020/9/17/0017 15:27
 *
 */
trait DAO {
    def readFile(path:String):RDD[_]

    def readFromKafka() = {
        val brokerList:String = PropertiesUtil.getValue("kafka-broker-list")
        val groupID = PropertiesUtil.getValue("kafka.group.id")
        val topic = PropertiesUtil.getValue("kafka.topic")

        //3.定义kafka参数
        val kafkaPara:Map[String,Object] = Map[String,Object](
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG.->(brokerList), //隐式转换转成了二元组
            (ConsumerConfig.GROUP_ID_CONFIG,groupID),
            "key.deserializer" -> "org.apache.kafka.common.serialization.StringDeserializer",
            "value.deserializer" -> "org.apache.kafka.common.serialization.StringDeserializer"
        )

        //4.读取kafka数据创建DStream
        val kafkaDStream  = KafkaUtils.createDirectStream[String,String](
            EnvUtil.getStreamingEnv,
            LocationStrategies.PreferConsistent,
            ConsumerStrategies.Subscribe[String, String](Set(topic), kafkaPara)
        )

        //5.将每条消息的KV取出
        kafkaDStream.map(x => x.value())

    }


    def writeToKafka(implicit data:()=> Seq[String]):Unit={
        val brokerList:String = ProUtils.getProperties("summer")("kafka-broker-list")
        val topic = PropertiesUtil.getValue("kafka.topic")
        val prop = new Properties()
        //创建配置对象
        //添加配置
        prop.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerList)
        prop.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
        prop.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")

        // 创建Kafka消费者
        val kafkaProducer: KafkaProducer[String, String] = new KafkaProducer[String, String](prop)

        while (true) {
            // 随机产生实时数据并通过Kafka生产者发送到Kafka集群中
            for (line <- data()) {
                kafkaProducer.send(new ProducerRecord[String, String](topic, line))
                println(line)

            }
            Thread.sleep(2000)
        }
    }
}
