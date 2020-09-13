package com.atguigu.apitest.sinktest

import com.atguigu.apitest.SensorReading
import org.apache.flink.api.common.serialization.SimpleStringEncoder
import org.apache.flink.core.fs.Path
import org.apache.flink.streaming.api.functions.sink.filesystem.StreamingFileSink
import org.apache.flink.streaming.api.scala._

/**
 * Created by John.Ma on 2020/9/12 0012 23:52
 */
object FileSinkTest {
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

        dataStream.print()
//        dataStream.writeAsCsv("D:\\fileImportant\\Learn_projects\\learn\\Flink_WuShengran\\src\\main\\resources\\out.txt")
        dataStream.addSink(StreamingFileSink.forRowFormat(
            new Path("D:\\fileImportant\\Learn_projects\\learn\\Flink_WuShengran\\src\\main\\resources\\out1.txt"),
            new SimpleStringEncoder[SensorReading]()
        ).build()
        )
        env.execute()
    }
}
