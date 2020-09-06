package com.vlion.source

import java.time.LocalDate

object Test{
    implicit class RichDay(num:Int){
        def days(stat:String):String={
            val today = LocalDate.now();
            if(stat == "age"){
                today.plusDays(-num).toString
            }else{
                today.plusDays(num).toString
            }
        }
    }

    def operation[T](arr:Array[T],op:T => T)={
        for( a <- arr) yield  op(a)
    }

    def main(args: Array[String]): Unit = {
        val ago= "ago"
        val later = "latter"
        print(2 days later)
        val aaa = operation[Int](Array( 1,2,3), _ + 1 )
        println(aaa)

    }
}
