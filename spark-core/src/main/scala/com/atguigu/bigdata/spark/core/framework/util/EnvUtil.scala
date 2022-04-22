package com.atguigu.bigdata.spark.core.framework.util

import org.apache.spark.SparkContext

/**
 * @author: malichun
 * @date 2022/4/21 0021 21:08
 */
object EnvUtil {
    private val scLocal = new ThreadLocal[SparkContext]()

    def put(sc:SparkContext): Unit ={
        scLocal.set(sc)
    }

    def take()={
        scLocal.get()
    }

    def clear():Unit={
        scLocal.remove()
    }
}
