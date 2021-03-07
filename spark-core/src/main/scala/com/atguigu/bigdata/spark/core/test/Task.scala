package com.atguigu.bigdata.spark.core.test

/**
 * Created by John.Ma on 2021/2/1 0001 22:11
 */
class Task extends Serializable {
    val datas = List(1,2,3,4)

    val logic: Int => Int = _ * 2

}
