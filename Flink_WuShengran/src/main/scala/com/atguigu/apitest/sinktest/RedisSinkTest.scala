package com.atguigu.apitest.sinktest

import com.atguigu.apitest.SensorReading
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.connectors.redis.RedisSink
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisPoolConfig
import org.apache.flink.streaming.connectors.redis.common.mapper.{RedisCommand, RedisCommandDescription, RedisMapper}

/**
 * Created by John.Ma on 2020/9/13 0013 1:35
 */
object RedisSinkTest {
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

    //定义一个FlinkJedisConfigBase
    val conf =new FlinkJedisPoolConfig.Builder()
        .setHost("172.16.189.210")
        .setPort(6379)
        .setDatabase(15)
        .build()



    dataStream.addSink(new RedisSink[SensorReading](conf,new MyRedisSinkMapper()))


    env.execute("redis sink test")
  }
}

//定义一个RedisMapper
class MyRedisSinkMapper extends RedisMapper[SensorReading]{

  //定义保存数据写入Reids的命令, HSET 表名 key value
  override def getCommandDescription: RedisCommandDescription = {
    new RedisCommandDescription(RedisCommand.HSET,"sensor_temp")
  }

  //将id指定为key
  override def getKeyFromData(data: SensorReading): String = {
    data.id
  }

  //指定value,将温度值指定为value
  override def getValueFromData(data: SensorReading): String = {
    data.temperature.toString
  }
}