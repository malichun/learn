package com.atguigu.bigdata.spark.core.framework.common

import com.atguigu.bigdata.spark.core.framework.util.EnvUtil

/**
 * @author: malichun
 * @date 2022/4/21 0021 21:02
 */
trait TDao {
    def readFile(path:String) = {
        val sc = EnvUtil.take()
        sc.textFile(path)
    }
}
