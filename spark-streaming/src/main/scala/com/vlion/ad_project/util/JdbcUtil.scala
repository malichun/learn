package com.vlion.ad_project.util

import java.sql.{Connection, PreparedStatement, ResultSet}
import java.util.Properties

import com.alibaba.druid.pool.DruidDataSourceFactory
import javax.sql.DataSource

object JdbcUtil {
    //初始化连接池
    var dataSource :DataSource = init()


    def init():DataSource={
        val properties = new Properties()
        val config:Properties = PropertiesUtil.load("config.properties")
        properties.setProperty("driverClassName", "com.mysql.jdbc.Driver")
        properties.setProperty("url", config.getProperty("jdbc.url"))
        properties.setProperty("username", config.getProperty("jdbc.user"))
        properties.setProperty("password", config.getProperty("jdbc.password"))
        properties.setProperty("maxActive", config.getProperty("jdbc.datasource.size"))
        DruidDataSourceFactory.createDataSource(properties)
    }

    //获取mysql连接
    def getConnection:Connection = {
        dataSource.getConnection()
    }

    //执行SQL语句,单条数据插入
    def executeUpdate(connection:Connection,sql:String,params:Array[Any]):Int={
        var rtn =0
        var pstmt:PreparedStatement = null

        try{
            connection.setAutoCommit(false)
            pstmt=connection.prepareStatement(sql)
            if(params!=null && params.length > 0 ){
                for(i<- params.indices){
                    pstmt.setObject(i + 1, params(i))
                }
            }
            rtn = pstmt.executeUpdate()
            connection.commit()
            pstmt.close()
        }catch{
            case e:Exception =>e.printStackTrace()
        }
        rtn
    }

    //执行SQL语句,批量数据插入
    def executeBatchUpdate(connection:Connection,sql:String,paramsList:Iterable[Array[Any]]):Array[Int]={
        var rtn :Array[Int] = null;
        var pstmt:PreparedStatement = null
        try{
            connection.setAutoCommit(false)
            pstmt = connection.prepareStatement(sql)
            for(params <- paramsList){
                if(params !=null  && params.length>0){
                    for(i <- params.indices){
                        pstmt.setObject(i+1,params(i))
                    }
                    pstmt.addBatch()
                }
            }
            rtn = pstmt.executeBatch()
            connection.commit()
            pstmt.close()
        }catch{
            case e:Exception => e.printStackTrace()
        }

        rtn
    }

    /**
     * 判断一条数据是否存在
     * @param connection
     * @param sql
     * @param params
     * @return
     */
    def isExists(connection:Connection,sql:String,params:Array[Any]):Boolean={
        var flag:Boolean = false
        var pstmt:PreparedStatement = null

        try{
            pstmt = connection.prepareStatement(sql)
            for (i <- params.indices) {
                pstmt.setObject(i + 1, params(i))
            }
            flag = pstmt.executeQuery().next()
            pstmt.close()
        }catch{
            case e:Exception => e.printStackTrace()
        }
            flag
    }

    //获取MySQL的一条数据
    def getDataFromMysql(connection: Connection, sql: String, params: Array[Any]): Long = {
        var result: Long = 0L
        var pstmt: PreparedStatement = null
        try {
            pstmt = connection.prepareStatement(sql)
            for (i <- params.indices) {
                pstmt.setObject(i + 1, params(i))
            }
            val resultSet: ResultSet = pstmt.executeQuery()
            while (resultSet.next()) {
                result = resultSet.getLong(1)
            }
            resultSet.close()
            pstmt.close()
        } catch {
            case e: Exception => e.printStackTrace()
        }
        result
    }

    def main(args: Array[String]): Unit = {
        val l = getDataFromMysql(getConnection, "select count(1) from user_ad_count", Array())
        println(l)
    }

}
