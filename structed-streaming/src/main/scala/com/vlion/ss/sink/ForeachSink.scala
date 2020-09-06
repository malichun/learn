package com.vlion.ss.sink

import java.sql.{Connection, DriverManager, PreparedStatement}

import org.apache.spark.sql.streaming.StreamingQuery
import org.apache.spark.sql.{DataFrame, ForeachWriter, Row, SparkSession}

object ForeachSink {
    def main(args: Array[String]): Unit = {
        val spark = SparkSession.builder().master("local[*]")
            .appName("mysqlSink")
            .getOrCreate()

        import spark.implicits._

        val lines:DataFrame = spark.readStream
            .format("socket")
            .option("host","www.bigdata05.com")
            .option("port",4444)
            .load()

        val wordCount = lines.as[String]
            .flatMap(_.split("\\W+"))
            .groupBy("value")
            .count()
            .repartition(1)


        val query:StreamingQuery = wordCount.writeStream
            .outputMode("update")
            // 使用 foreach 的时候, 需要传递ForeachWriter实例, 三个抽象方法需要实现. 每个批次的所有分区都会创建 ForeeachWriter 实例
            .foreach(new ForeachWriter[Row]{
                var conn:Connection = _
                var ps:PreparedStatement = _
                val batchCount = 0

                // 一般用于 打开链接. 返回 false 表示跳过该分区的数据,
                override def open(partitionId: Long, epochId: Long): Boolean = {
                    println("open..." + partitionId+" "+epochId)
                    Class.forName("com.mysql.jdbc.Driver")
                    conn = DriverManager.getConnection("jdbc:mysql://172.16.189.204:3306/test","VLION_ZEBRA","VLION_ZEBRA")
                    val sql="insert into word_count values(?,?) on duplicate key update word=?,count=?"
                    ps = conn.prepareStatement(sql)
                    conn != null && !conn.isClosed && ps!=null
                }
                //把数据写入到连接
                override def process(value: Row): Unit = {
                    println("process ...." + value)
                    val word:String = value.getString(0)
                    val count:Long = value.getLong(1)

                    ps.setString(1,word)
                    ps.setLong(2,count)
                    ps.setString(3,word)
                    ps.setLong(4,count)
                    ps.execute()
                }
                //用户关闭连接
                override def close(errorOrNull: Throwable): Unit = {
                    println("close...")
                    ps.close()
                    conn.close()
                }
            }).start

        query.awaitTermination()


    }
}
