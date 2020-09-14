package com.atguigu.apitest

import org.apache.flink.api.common.state.{ValueState, ValueStateDescriptor}
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.KeyedProcessFunction
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.util.Collector


/**
 * Created by John.Ma on 2020/9/14 0014 22:47
 */
object ProcessFunctionTest2 {
    def main(args: Array[String]): Unit = {
        val env = StreamExecutionEnvironment.getExecutionEnvironment
        env.setParallelism(1)

        // 读取数据
        val inputStream = env.socketTextStream("www.bigdata01.com", 4444)

        // 先转换成样例类类型（简单转换操作）
        val dataStream = inputStream
            .map( data => {
                val arr = data.split(",")
                SensorReading(arr(0), arr(1).toLong, arr(2).toDouble)
            } )
//            .keyBy(_.id)
//            .process(new MyKeyedProcessFunction)


        //需求:10s内温度连续上升报警
        val warningStream = dataStream
            .keyBy(_.id)
            .process( new TempIncreWarning(10000) ) //毫秒数
        warningStream.print()

        env.execute()


    }
}

//需求:10s内温度连续上升报警
class TempIncreWarning(interval:Long) extends KeyedProcessFunction[String,SensorReading,String]{
    //定义状态,保存上一次温度值进行比较,保存注册定时器的时间戳用于删除
    lazy val lastTempState:ValueState[Double] = getRuntimeContext.getState(new ValueStateDescriptor("time-stat",classOf[Double]))
    lazy val timeTsState:ValueState[Long] = getRuntimeContext.getState(new ValueStateDescriptor("time-ts",classOf[Long]))

    //每一条数据来都会调用这个方法
    override def processElement(value: SensorReading, ctx: KeyedProcessFunction[String, SensorReading, String]#Context, out: Collector[String]): Unit = {
        //先把上一次温度拿过来
        val lastTemp = lastTempState.value()
        val timeTs = timeTsState.value()

        // 更新温度值
        lastTempState.update(value.temperature)

        //判断当前温度值和上次温度进行比较
        if(value.temperature > lastTemp && timeTs == 0){
            //如果温度上升,且没有定时器,那么注册当前时间戳10s之后的定时器
            val ts = ctx.timerService().currentProcessingTime() + interval
            ctx.timerService().registerProcessingTimeTimer(ts)
            timeTsState.update(ts)
        }else if(value.temperature < lastTemp){
            //如果温度下降,那么删除定时器
            ctx.timerService().deleteProcessingTimeTimer(timeTs)
            timeTsState.clear()
        }
    }

    //定时器触发,触发了这个就表示已经过了10秒了
    override def onTimer(timestamp: Long, ctx: KeyedProcessFunction[String, SensorReading, String]#OnTimerContext, out: Collector[String]): Unit = {
        out.collect("传感器"+ctx.getCurrentKey + "的温度连续" + interval/1000+"秒连续上升")
        timeTsState.clear()
    }


}


/**
 *  KeyedProcessFunction功能测试
 */
class MyKeyedProcessFunction extends KeyedProcessFunction[String, SensorReading, String]{
    var myState: ValueState[Int] = _

    override def open(parameters: Configuration): Unit = {
        myState = getRuntimeContext.getState(new ValueStateDescriptor[Int]("mystate", classOf[Int]))
    }

    override def processElement(value: SensorReading, ctx: KeyedProcessFunction[String, SensorReading, String]#Context, out: Collector[String]): Unit = {
        ctx.getCurrentKey
        ctx.timestamp()
        ctx.timerService().currentWatermark()
        ctx.timerService().registerEventTimeTimer(ctx.timestamp() + 60000L) //可以有多个定时器,下面的方法onTimer执行
//        ctx.timerService().deleteEventTimeTimer() //删除timer
    }

    override def onTimer(timestamp: Long, ctx: KeyedProcessFunction[String, SensorReading, String]#OnTimerContext, out: Collector[String]): Unit = {

    }
}