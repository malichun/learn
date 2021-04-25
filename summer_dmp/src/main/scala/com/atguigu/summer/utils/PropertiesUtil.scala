package com.atguigu.summer.utils

import java.util.ResourceBundle

/**
 * @description:
 * @author: malichun
 * @time: 2020/9/17/0017 15:36
 *
 */
object PropertiesUtil {
    val summer:ResourceBundle = ResourceBundle.getBundle("summer")//summer.properties

    def getValue(key:String):String={
        val str:String = summer.getString(key)
        str
    }
}
