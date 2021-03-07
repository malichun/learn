package com.atguigu.bigdata.spark.core.test

/**
 * Created by John.Ma on 2021/2/1 0001 22:13
 */
class SubTask extends Serializable {

    var datas: List[Int] = _
    var logic :Int => Int = _

    def compute()={
        datas.map(logic)
    }
}
