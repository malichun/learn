package com.vlion.test

import org.apache.spark.{SparkConf, SparkContext}
import redis.clients.jedis.{Jedis, JedisPool, JedisPoolConfig}

import scala.beans.BeanProperty

/**
 * @description:
 * @author: malichun
 * @time: 2020/11/18/0018 15:05
 *
 */
object RedisUtil extends Serializable {
    val jedisPoolConfig: JedisPoolConfig = new JedisPoolConfig()
    jedisPoolConfig.setMaxTotal(100) //最大连接数
    jedisPoolConfig.setMaxIdle(20) //最大空闲
    jedisPoolConfig.setMinIdle(20) //最小空闲
    jedisPoolConfig.setBlockWhenExhausted(true) //忙碌时是否等待
    jedisPoolConfig.setMaxWaitMillis(500) //忙碌时等待时长 毫秒
    jedisPoolConfig.setTestOnBorrow(true) //每次获得连接的进行测试
    val jedisPool: JedisPool = new JedisPool(jedisPoolConfig, "172.16.189.215", 6379)

    // 直接得到一个 Redis 的连接
    def getJedisClient: Jedis = {
        jedisPool.getResource
    }

//    val client = RedisUtil.getJedisClient
//    client.exists("")
//
//    if (client != null) client.close()
}


object Test {



    def main(args: Array[String]): Unit = {
        val conf = new SparkConf()
        val sc = new SparkContext(conf)

        val rdd25703 = sc.textFile("/tmp/test/20201118_cheap_data_statics/1_25703.txt")

        rdd25703.map(line => {
            val arr = line.split(",")
            (arr(0), "ct:" + arr(0) + ":" + arr(1))
        }).combineByKey[Int](
            (v:String) => {
            val client = new Jedis("172.16.189.215", 6379)
            val isExists = client.exists(v)
            val result = if (isExists) 1 else 0
            if (client != null) client.close()
            result
        },
            (c:Int,v:String) => {
                val client = new Jedis("172.16.189.215", 6379)
                val isExists = client.exists(v)

                if (client != null) client.close()
                c + (if (isExists) 1 else 0)
            },
            (c1:Int,c2:Int) =>c1+c2
            , 200
        )


}

}

class atm{
    var money = 0
}
object Test41{
    def main(args: Array[String]): Unit = {
        /*
        * scala在定义字段时使用var,默认生成对应setter和getter方法
        * */
        var c = new atm()
        //调用了scala的setter方法,对应的名称money_=
        c.money_=(100)
        //调用了scala的getter方法,对应的名称就是money
        println(c.money)

    }


}