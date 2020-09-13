package com.atguigu.apitest.sinktest

import java.util

import com.atguigu.apitest.SensorReading
import org.apache.flink.api.common.functions.RuntimeContext
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.connectors.elasticsearch.{ElasticsearchSinkFunction, RequestIndexer}
import org.apache.flink.streaming.connectors.elasticsearch6.ElasticsearchSink
import org.apache.http.HttpHost
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.client.Requests

/**
 * Created by John.Ma on 2020/9/13 0013 8:59
 */
object EsSinTest {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)
    //读取数据
    val inputStream = env.readTextFile("D:\\fileImportant\\Learn_projects\\learn\\Flink_WuShengran\\src\\main\\resources\\sensor.txt")



    /**
     * 先转换成样例类类型(简单转换操作)
     */
    val dataStream = inputStream
      .map(data => {
        val arr = data.split(",")
        SensorReading(arr(0), arr(1).toLong, arr(2).toDouble)
      })

    //定义HttpHosts
    val httpHosts =new util.ArrayList[HttpHost](){
      add(new HttpHost("localhost",9200))
    }

    //自动以写入es的EsSinkFunction
    val myEsSinkFunc = new ElasticsearchSinkFunction[SensorReading] {
      //每来一次调用这个方法
      override def process(t: SensorReading, runtimeContext: RuntimeContext, requestIndexer: RequestIndexer): Unit = {
        //包装一个Map作为dataSource
        val dataSource = new util.HashMap[String,String]()
        dataSource.put("id",t.id)
        dataSource.put("temperature",t.temperature.toString)
        dataSource.put("ts",t.timestamp.toString)

        //创建indexRequest用于发送http请求
        val indexRequest: IndexRequest = Requests.indexRequest()
          .index("sensor")
          .`type`("readingData")
          .source(dataSource)

        //用indexer发送请求
        requestIndexer.add(indexRequest)

      }
    }

    dataStream.addSink(new ElasticsearchSink
      .Builder[SensorReading](httpHosts, myEsSinkFunc)
      .build()
    )


    env.execute("es sink test")

  }
}
