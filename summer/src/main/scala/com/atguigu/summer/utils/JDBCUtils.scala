package com.atguigu.summer.utils

import java.sql.Connection

import javax.sql.DataSource
import java.util

import com.alibaba.druid.pool.DruidDataSourceFactory

/**
 * @description:
 * @author: malichun
 * @time: 2020/9/17/0017 15:37
 *
 */
object JDBCUtils {

    var dataSource: DataSource = init()

    def init(): DataSource = {
        val paramMap = new util.HashMap[String, String]()
        paramMap.put("driverClassName", PropertiesUtil.getValue("jdbc.driver.name"))
        paramMap.put("url", PropertiesUtil.getValue("jdbc.url"))
        paramMap.put("username", PropertiesUtil.getValue("jdbc.username"))
        paramMap.put("password", PropertiesUtil.getValue("jdbc.password"))
        paramMap.put("maxActive", PropertiesUtil.getValue("jdbc.datasource.size"))

        val source: DataSource = DruidDataSourceFactory.createDataSource(paramMap)
        source
    }

    def getConnection: Connection = {
        dataSource.getConnection()
    }
}
