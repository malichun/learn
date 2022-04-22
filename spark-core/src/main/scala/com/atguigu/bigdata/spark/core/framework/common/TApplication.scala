package com.atguigu.bigdata.spark.core.framework.common

import com.atguigu.bigdata.spark.core.framework.controller.WordCountController
import com.atguigu.bigdata.spark.core.framework.util.EnvUtil
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @author: malichun
 * @date 2022/4/21 0021 20:53
 */
trait TApplication {

    def start(master: String="local[*]", app:String="Application")(op : => Unit): Unit ={
        val conf = new SparkConf().setMaster(master).setAppName(app)
        val sc = new SparkContext(conf)
        EnvUtil.put(sc)
        try{
            op
        }catch{
            case ex:Exception => println(ex.getMessage)
        }
        sc.stop()
        EnvUtil.clear()
    }

}
