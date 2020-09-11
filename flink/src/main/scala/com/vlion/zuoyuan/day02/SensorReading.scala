package com.vlion.zuoyuan.day02

/**
 * Created by John.Ma on 2020/9/9 0009 0:14
 * 传感器读书的数据结构
 */
case class SensorReading(id: String, // 传感器id
                         timestamp: Long, //时间戳
                         temperature: Double //温度
                        )
