package com.vlion.test

object ClassTest {
    def main(args: Array[String]): Unit = {
        println("123")
    }
}

class Person(name:String){
    var age:Int = _
    def this(name:String,age:Int){
        this(name)
        this.age = age
    }

}