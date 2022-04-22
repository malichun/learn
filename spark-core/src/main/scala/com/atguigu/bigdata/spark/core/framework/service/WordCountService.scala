package com.atguigu.bigdata.spark.core.framework.service

import com.atguigu.bigdata.spark.core.framework.common.TService
import com.atguigu.bigdata.spark.core.framework.dao.WordCountDao

/**
 * 服务层
 */
class WordCountService extends TService{
    private val wordCountDao = new WordCountDao()

    // 数据分析
    def dataAnalysis(): Array[(String, Int)]={
        val lines = wordCountDao.readFile("data.txt")

        val wordRDD = lines.flatMap(_.split(" "))

        val tupleRDD = wordRDD.map((_,1))


        val wordToSum = tupleRDD.reduceByKey(_ + _)

        val array = wordToSum.collect()
        array
    }
}
