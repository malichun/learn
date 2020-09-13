package com.atguigu.apitest

import org.apache.flink.api.common.functions.{FilterFunction, MapFunction, ReduceFunction, RichMapFunction}
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.scala._

/**
 * Created by John.Ma on 2020/9/12 0012 17:23
 */
object TransformTest {
    def main(args: Array[String]): Unit = {

        val env = StreamExecutionEnvironment.getExecutionEnvironment
        //0.读取数据
        val inputStream = env.readTextFile("D:\\fileImportant\\Learn_projects\\learn\\Flink_WuShengran\\src\\main\\resources\\sensor.txt")

        /**
         * 1.先转换成样例类类型(简单转换操作)
         */
        val dataStream = inputStream
            .map(data => {
                val arr = data.split(",")
                SensorReading(arr(0), arr(1).toLong, arr(2).toDouble)
            })

        /**
         * 2.分组聚合,输出每个传感器当前最小值
         */
         val aggStream =    dataStream
                 .keyBy("id") //根据id进行分组
                 .minBy("temperature")

//         aggStream.print()

        /**
         * 3.需要输出当前最小的温度值,以及最近的时间戳,要用reduce
         */
        //
        dataStream
            .keyBy("id") //针对每一个key取最小!!!!!
            .reduce((s1, s2) =>
                SensorReading(s1.id, s1.timestamp max s2.timestamp, s1.temperature.min(s2.temperature))
            )
           // .print()
        //或者使用接口实现类
//        dataStream
//            .keyBy("id") //针对每一个key取最小!!!!!
//            .reduce(new MyReduceFunction)  //使用接口实现类
//            .print()

        /**
         * 4.多流转换操作  split和select
         */
    //4.1需求:传感器数据按照温度高低(以30度为界),拆成两个流
        val splitStream: SplitStream[SensorReading] = dataStream
            .split(data =>
                if(data.temperature > 30.0) Seq("high") else Seq("low")
            ) //返回一个SplitStream

        val highTempStream = splitStream.select("high")
        val lowTempStream = splitStream.select("low")
        val allTempStream = splitStream.select("high","low")

        //highTempStream.print("high")
        //lowTempStream.print("low")
        //allTempStream.print("all")
        //结果
        //high:2> SensorReading(sensor_1,1547718300,32.0)
        //all:2> SensorReading(sensor_1,1547718300,32.0)
        //high:2> SensorReading(sensor_1,1547720000,36.2)
        //all:2> SensorReading(sensor_1,1547720000,36.2)
        //all:1> SensorReading(sensor_7,1547718202,6.7)
        //low:1> SensorReading(sensor_7,1547718202,6.7)

    //4.2 合流 connect coMap
        val warningStream: DataStream[(String, Double)] = highTempStream.map(data => (data.id,data.temperature))

        val connectedStreams: ConnectedStreams[(String, Double), SensorReading] = warningStream.connect(lowTempStream)
        //用coMap对数据进行分别处理
        val coMapResultStream: DataStream[Product] = connectedStreams
                .map(
                    warningData => (warningData._1,warningData._2,"warning"),
                    lowTempData => (lowTempData.id,"healthy")

                )
        //coMapResultStream.print("coMap")
        //结果
        //coMap:3> (sensor_10,38.1,warning)
        //coMap:3> (sensor_7,healthy)
        //coMap:4> (sensor_1,32.0,warning)

    //4.3 union合流
        //直接返回DataStream
        val unionStream: DataStream[SensorReading] = highTempStream.union(lowTempStream)
        unionStream.print()
        env.execute("transform test")


    }
}

class MyReduceFunction extends ReduceFunction[SensorReading] {
    override def reduce(value1: SensorReading, value2: SensorReading): SensorReading = {
        SensorReading(value1.id, value2.timestamp.max(value2.timestamp), value1.temperature.min(value2.temperature))
    }
}

//自定义一个函数类
class MyFilter extends FilterFunction[SensorReading]{
    override def filter(value: SensorReading): Boolean = {
        value.id.startsWith("sensor_1")
    }
}

// 普通函数
class MyMapper extends MapFunction[SensorReading,String]{
    override def map(value: SensorReading): String = {
        value.id+" temperature"
    }
}

// 富函数,可以获取到运行时上下文,还有一些生命周期
class MyRichMapper extends RichMapFunction[SensorReading,String]{

    override def open(parameters: Configuration): Unit = {
        //做一些初始化的操作,比如数据库的连接
//        getRuntimeContext //获取当前状态,状态编程
    }

    override def close(): Unit = {
        //一般做收尾工作,比如关闭数据库连接,或者清空状态

    }

    override def map(value: SensorReading): String = {
        value.id+" temperature"
    }
}

