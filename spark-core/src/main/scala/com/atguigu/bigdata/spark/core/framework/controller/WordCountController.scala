package com.atguigu.bigdata.spark.core.framework.controller

import com.atguigu.bigdata.spark.core.framework.common.TController
import com.atguigu.bigdata.spark.core.framework.service.WordCountService

/**
 * 控制层
 */
class WordCountController extends TController{
    private val wordCountService = new WordCountService()

    // 调度
    def dispatch():Unit = {
        val array = wordCountService.dataAnalysis()
        array.foreach(println)
    }

}
