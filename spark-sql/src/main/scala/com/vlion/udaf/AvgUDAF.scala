package com.vlion.udaf

/**
 * @description:
 * @author: malichun
 * @time: 2021/2/3/0003 19:25
 *
 */
object AvgUDAF {
    self =>

    def apply(name:String,age:Int): AvgUDAF = {
        val a = new AvgUDAF
        a.name = name
        a.age = age
        a
    }

    def main(args: Array[String]): Unit = {
        println(AvgUDAF("张三",10))
    }


}

class AvgUDAF(){
    var name:String = _
    var age:Int = _

    override def toString: String = s"name:${name}, age:$age"
}


