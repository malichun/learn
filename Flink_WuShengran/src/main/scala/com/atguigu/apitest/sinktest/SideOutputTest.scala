package com.atguigu.apitest.sinktest

import com.atguigu.apitest.SensorReading
import org.apache.flink.contrib.streaming.state.RocksDBStateBackend
import org.apache.flink.runtime.state.filesystem.FsStateBackend
import org.apache.flink.runtime.state.memory.MemoryStateBackend
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.apache.flink.streaming.api.scala._
import org.apache.flink.util.Collector

/**
 * Created by John.Ma on 2020/9/14 0014 23:51
 *
 * 测试侧输出流
 */
object SideOutputTest {
    def main(args: Array[String]): Unit = {
        val env = StreamExecutionEnvironment.getExecutionEnvironment
        env.setParallelism(1)
//        env.setStateBackend(new MemoryStateBackend())
//        env.setStateBackend(new FsStateBackend("")) //
//        env.setStateBackend(new RocksDBStateBackend())

        // 读取数据
        val inputStream = env.socketTextStream("www.bigdata01.com", 4444)

        // 先转换成样例类类型（简单转换操作）
        val dataStream = inputStream
            .map(data => {
                val arr = data.split(",")
                SensorReading(arr(0), arr(1).toLong, arr(2).toDouble)
            })

        //分流操作
        val highTempStream = dataStream
            .process(new SplitTempProcessor(30.0))

        highTempStream.print("high")

        highTempStream.getSideOutput(new OutputTag[(String, Long, Double)]("low")).print("low")


        env.execute("side output test")

    }
}

//实现自定义processFunction,进行分流
class SplitTempProcessor(threshold: Double) extends ProcessFunction[SensorReading, SensorReading] { //主 流的输出类型

    override def processElement(value: SensorReading, ctx: ProcessFunction[SensorReading, SensorReading]#Context, out: Collector[SensorReading]): Unit = {
        if (value.temperature > threshold) { //filter,还可以换其他类型
            //如果当前温度值 > 30,那么输出到主流
            out.collect(value)
        } else {
            //如果不超过30度,那么输出到侧输出流,用上下文
            ctx.output(new OutputTag[(String, Long, Double)]("low"), (value.id, value.timestamp, value.temperature))

        }
    }
}
