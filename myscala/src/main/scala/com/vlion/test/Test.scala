package com.vlion.test

import scala.collection.mutable

object Test {
    def main(args: Array[String]): Unit = {

        val map1 = mutable.Map("a" -> 1, "b" -> 3, "c" -> 5)

        val map2 = mutable.Map("b" -> 1, "c" -> 3, "d" -> 5)

        map1.foldLeft(map2)((m, kv) => {
            val key = kv._1
            val value = kv._2
            m.update(key, m.getOrElse(key, 0) + value)
            m
        })

        println(map2)

        println(add(2, 3, 4, 5))

        print(foo)

        print(*(3))
    }

    def *(a: Int) = {
        a * 2
    }

    def foo() = {
        200
    }

    def add(a: Int*) = {
        a.reduce(_ - _)
    }
}
