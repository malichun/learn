package com.atguigu.apitest.sinktest

import java.sql.{Connection, DriverManager, PreparedStatement}

import com.atguigu.apitest.SensorReading
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.sink.{RichSinkFunction, SinkFunction}
import org.apache.flink.streaming.api.scala._

/**
 * Created by John.Ma on 2020/9/13 0013 9:42
 */
object JDBCSinkTest {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)
    //读取数据
    val inputStream = env.readTextFile("D:\\fileImportant\\Learn_projects\\learn\\Flink_WuShengran\\src\\main\\resources\\sensor.txt")

    /**
     * 先转换成样例类类型(简单转换操作)
     */
    val dataStream = inputStream
      .map(data => {
        val arr = data.split(",")
        SensorReading(arr(0), arr(1).toLong, arr(2).toDouble)
      })

    dataStream.addSink(new MyJdbcSinkFunc())


    env.execute("jdbc sink Test")
  }
}

//实现RichFunction,包含初始化的方法
class MyJdbcSinkFunc extends RichSinkFunction[SensorReading] {
  //定义连接,预编译语句
  var conn: Connection = _
  var insertStmt: PreparedStatement = _
  var updateStmt: PreparedStatement = _

  override def open(parameters: Configuration): Unit = {
    conn = DriverManager.getConnection("jdbc:mysql://172.16.189.204:3306/test_mydata", "VLION_MAMMU", "VLION_MAMMU")
    insertStmt = conn.prepareStatement("insert into sensor_temp (id, temp) values (?, ?)")
    updateStmt = conn.prepareStatement("update sensor_temp set temp = ? where id = ?")
  }

  override def close(): Unit = {
    insertStmt.close()
    updateStmt.close()
    conn.close()
  }

  override def invoke(value: SensorReading, context: SinkFunction.Context[_]): Unit = {
    // 先执行更新操作，查到就更新
    updateStmt.setDouble(1, value.temperature)
    updateStmt.setString(2, value.id)
    updateStmt.execute()
    // 如果更新没有查到数据，那么就插入
    if( updateStmt.getUpdateCount == 0 ){
      insertStmt.setString(1, value.id)
      insertStmt.setDouble(2, value.temperature)
      insertStmt.execute()
    }
  }
}
