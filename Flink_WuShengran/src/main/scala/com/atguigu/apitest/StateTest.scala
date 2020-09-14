package com.atguigu.apitest

import java.{lang, util}

import org.apache.flink.api.common.functions.{IterationRuntimeContext, RichFlatMapFunction, RichFunction, RichMapFunction, RuntimeContext}
import org.apache.flink.api.common.state.{ListState, ListStateDescriptor, MapState, MapStateDescriptor, ReducingState, ReducingStateDescriptor, ValueState, ValueStateDescriptor}
import org.apache.flink.configuration.Configuration
import org.apache.flink.contrib.streaming.state.RocksDBStateBackend
import org.apache.flink.runtime.state.filesystem.FsStateBackend
import org.apache.flink.streaming.api.scala._
import org.apache.flink.util.Collector

object StateTest {
    def main(args: Array[String]): Unit = {
        val env = StreamExecutionEnvironment.getExecutionEnvironment
        env.setParallelism(1)

        val inputStream = env.socketTextStream("www.bigdata01.com", 4444)

        //1.先转换成样例类类型(简单转换操作)
        val dataStream = inputStream
            .map(data => {
                val arr = data.split(",")
                SensorReading(arr(0), arr(1).toLong, arr(2).toDouble)
            })


        //需求:对于温度传感器温度值跳变超过10度,报警
        val alterStream = dataStream
                .keyBy(_.id)
//                .flatMap(new TempChangeAlert(10.0)) //阈值,从外部传进去,自定义
            //  def flatMapWithState[R: TypeInformation, S: TypeInformation](
            //        fun: (T, Option[S]) => (TraversableOnce[R], Option[S])): DataStream[R] = {
                .flatMapWithState[(String,Double,Double),Double] { //只有keyStream才有
                    case (data:SensorReading,None) => (List.empty,Some(data.temperature)) //第一次进来
                    case (data:SensorReading,lastTemp:Some[Double]) =>
                        //跟最新的温度值求差值,做比较
                        val diff = (data.temperature - lastTemp.get).abs
                        if(diff > 10.0){
                            (List((data.id,lastTemp.get,data.temperature)),Some(data.temperature))
                        }else{
                            (List.empty,Some(data.temperature))
                        }
                }  // 有状态的FlatMap

        alterStream.print()


        env.execute("state test")


    }
}

//实现自定义RichFlatMapFunction
class TempChangeAlert(threshold: Double) extends RichFlatMapFunction[SensorReading,(String,Double,Double)]{
    //定义状态,保存上一次的温度值,ValueState
    lazy val lastTempState:ValueState[Double] = getRuntimeContext.getState(
        new ValueStateDescriptor[Double]("last-temp",classOf[Double])
    )
    //判断是不是第一次进来,布尔,方式
    lazy val flagState:ValueState[Boolean] = getRuntimeContext.getState(
        new ValueStateDescriptor[Boolean]("flag-state",classOf[Boolean])
    )

    override def flatMap(value: SensorReading, out: Collector[(String, Double, Double)]): Unit = {
        //获取上次的温度值
        val lastTemp = lastTempState.value()

        //跟最新的温度值求差值,做比较
        val diff = (value.temperature - lastTemp).abs
        if(diff > threshold && flagState.value()){
            out.collect((value.id,lastTemp,value.temperature)) //想输出就输出,不输出就不输出FlatMap方便些
        }

        //更新状态
        lastTempState.update(value.temperature)
        flagState.update(false)
    }
}



/**
 * 测试:
 * Keyed state测试:必须定义在RichFunction中,因为需要运行时上下文
 */
class MyRichMapper1 extends RichMapFunction[SensorReading, String] {
    var valueState: ValueState[Double] = _

    lazy val listState: ListState[Int] = getRuntimeContext.getListState(
        new ListStateDescriptor[Int]("liststate", classOf[Int])
    )

    lazy val mapState: MapState[String, Double] = getRuntimeContext.getMapState(
        new MapStateDescriptor[String, Double]("mapstate", classOf[String], classOf[Double])
    )

    lazy val reducingState: ReducingState[SensorReading] = getIterationRuntimeContext.getReducingState(
        new ReducingStateDescriptor[SensorReading]("reducingState", new MyReduceFunction, classOf[SensorReading])
    )

    override def open(parameters: Configuration): Unit = {
        valueState = getRuntimeContext.getState(
            new ValueStateDescriptor[Double]("valuestate", classOf[Double])
        )
    }


    override def map(value: SensorReading): String = {
        // 状态的读写
        //valueState
        val myV = valueState.value()
        valueState.update(value.temperature)

        //listState
        listState.add(1)
        val list = new util.ArrayList[Int]() {
            add(1);
            add(2);
        }

        listState.addAll(list)
        listState.update(list)
        val ints: lang.Iterable[Int] = listState.get()


        //mapState
        mapState.contains("sensor_1")
        mapState.get("sensor_1")
        mapState.put("sensort_1", 1.3)
        mapState.keys()
        mapState.entries()
        mapState.remove("sensor_1")

        //reduceState
        reducingState.get()
        reducingState.add(value) // 调用reduceFunction做一次聚合

        value.id
    }
}




