package com.atguigu.summer.utils

import java.util.ResourceBundle

/**
 * @description: 自定义读取配置文件的工具类
 * @author: malichun
 * @time: 2020/9/17/0017 15:30
 *
 *
 */
object ProUtils {

  //从Summer中或为配置文件的名字,然后获取属性
    def getProperties(proName:String)(key:String):String={
        val bundle:ResourceBundle = ResourceBundle.getBundle(proName)

        val str:String = bundle.getString(key)

        str
    }

}
