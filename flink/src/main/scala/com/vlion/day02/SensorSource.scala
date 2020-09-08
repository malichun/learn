package com.vlion.day02

import java.util.Calendar

import org.apache.flink.streaming.api.functions.source.SourceFunction.SourceContext
import org.apache.flink.streaming.api.functions.source.{RichParallelSourceFunction, SourceFunction}

import scala.util.Random

/**
 * Created by John.Ma on 2020/9/9 0009 0:20
 * 自定义传感器 的 输入源
 * 泛型是SensorReading,表名产生的事件类型是SensorReading
 */
class SensorSource extends RichParallelSourceFunction[SensorReading]{

    //表示数据源是否正常运行,默认是运行
    var running = true

    //上下文参数用来发送数据
    override def run(ctx: SourceContext[SensorReading]): Unit = {
        val rand = new Random()

        var curFTemp = (1 to 10).map(
            //使用高斯噪声产生随机温度值
            i => ("sensor_"+i,(rand.nextGaussian()))
        )

        while(running) {
            curFTemp = curFTemp.map(t => (t._1, t._2 + (rand.nextGaussian()) * 0.5))


            //产生ms为单位的时间戳
            val curTime = Calendar.getInstance().getTimeInMillis

            //使用ctx参数的collect方法发射传感器数据
            curFTemp.foreach(t => ctx.collect(SensorReading(t._1, curTime, t._2)))

            //每隔100ms发送一条传感器数据
            Thread.sleep(100)
        }
    }

    /**
     * 定义当取消flink时,需要关闭数据源
     */
    override def cancel(): Unit = {
        running = false
    }
}
