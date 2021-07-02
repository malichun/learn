package com.atguigu.apitest.window;

import com.atguigu.apitest.beans.SensorReading;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.OutputTag;

/**
 * 测试滑动计数窗口的增量聚合函数
 *
 * 这里获取每个窗口里的温度平均值
 */
public class WindowTest2_CountWindow {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        //执行环境并行度设置为1
//        env.setParallelism(1);
        //从文件中获取数据输出
//        DataStream<String> inputStream = env.readTextFile("E:\\gitdir\\learn_projects\\myLearn\\Flink_WuShengranJava\\src\\main\\resources\\sensor.txt");
        DataStream<String> inputStream = env.socketTextStream("www.bigdata01.com", 7777);

        DataStream<SensorReading> dataStream = inputStream.map(s -> {
            String[] fields = s.split(",");
            return new SensorReading(fields[0], new Long(fields[1]), new Double(fields[2]));
        });

        DataStream<Double> resultStream = dataStream
            .keyBy(SensorReading::getId)
            .countWindow(10,2)
            .aggregate(new MyAvgFunc());


        resultStream.print("result");


        // 3.其他可选API
        OutputTag<SensorReading> outputTag = new OutputTag<SensorReading>("late") ;
        SingleOutputStreamOperator<Double> sumStream = dataStream.keyBy(SensorReading::getId)
            .window(TumblingProcessingTimeWindows.of(Time.seconds(5)))
//            .trigger()
//            .evictor()
            .allowedLateness(Time.seconds(5)) // 之前窗口还没关闭
            .sideOutputLateData(outputTag) // 漏网之鱼
            .aggregate(new MyAvgFunc());

        sumStream
            .print();

        // 迟到数据
        DataStream<SensorReading> sideOutput = sumStream.getSideOutput(outputTag);

        env.execute();
    }

    public static class MyAvgFunc implements AggregateFunction<SensorReading, Tuple2<Double,Integer>, Double> { //<IN, ACC, OUT>
        @Override
        public Tuple2<Double, Integer> createAccumulator() {
            return Tuple2.of(0.0,0);
        }

        @Override
        public Tuple2<Double, Integer> add(SensorReading value, Tuple2<Double, Integer> accumulator) {
            return Tuple2.of(accumulator.f0 + value.getTemperature(),
            accumulator.f1 + 1);
        }

        @Override
        public Double getResult(Tuple2<Double, Integer> accumulator) {
            return accumulator.f0 / accumulator.f1;
        }

        @Override
        public Tuple2<Double, Integer> merge(Tuple2<Double, Integer> a, Tuple2<Double, Integer> b) {
            return Tuple2.of(a.f0+b.f0, a.f1+b.f1);
        }
    }
}
