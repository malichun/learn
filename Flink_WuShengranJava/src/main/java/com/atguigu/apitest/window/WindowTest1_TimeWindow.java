package com.atguigu.apitest.window;

import com.atguigu.apitest.beans.SensorReading;
import org.apache.commons.collections.IteratorUtils;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

/**
 * 5秒内最大温度
 */
public class WindowTest1_TimeAndCountWindow {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        //执行环境并行度设置为1
//        env.setParallelism(1);
        //从文件中获取数据输出
//        DataStream<String> inputStream = env.readTextFile("E:\\gitdir\\learn_projects\\myLearn\\Flink_WuShengranJava\\src\\main\\resources\\sensor.txt");
        DataStream<String> inputStream = env.socketTextStream("www.bigdata01.com", 7777);

        DataStream<SensorReading> sensorStream = inputStream.map(s -> {
            String[] fields = s.split(",");
            return new SensorReading(fields[0], new Long(fields[1]), new Double(fields[2]));
        });

        /** TODO 一.窗口分配器 */

//        // WindowALL 只会往一个分区塞
//        sensorStream.windowAll(TumblingProcessingTimeWindows.of(Time.seconds(5)));

        // TODO 1.滚动窗口 .window(TumblingProcessingTimeWindows.of(Time.seconds(5)))
//        sensorStream
//            .keyBy(SensorReading::getId)
//            .window(TumblingProcessingTimeWindows.of(Time.seconds(5))) // 添加滚动窗口
//            .maxBy("temperature")
//            .print();

//        // TODO 2.滑动窗口  .window(SlidingProcessingTimeWindows.of(Time.seconds(10),Time.seconds(2)))
//        sensorStream
//            .keyBy(SensorReading::getId)
//            .window(SlidingProcessingTimeWindows.of(Time.seconds(10),Time.seconds(2))) // 添加滑动窗口窗口
//            .maxBy("temperature")
//            .print();

        // TODO 3.Session 窗口
//        sensorStream
//            .keyBy(SensorReading::getId)
//            .window(ProcessingTimeSessionWindows.withGap(Time.seconds(5)))
//            .max("temperature")
//            .print();


        // TODO 4. CountWindow
//        sensorStream.keyBy(SensorReading::getId)
//            .countWindow(5) // 5个窗口滚动,每个key中的窗口互不影响
//            .max("temperature")
//            .print();

        /** TODO 二.窗口函数 */

        // TODO 1. 增量聚合窗口

        // TODO Aggregate
//        sensorStream.keyBy(SensorReading::getId)
//            .window(TumblingProcessingTimeWindows.of(Time.seconds(5)))
//            .aggregate(new AggregateFunction<SensorReading, Integer, Integer>() {  //IN, ACC, OUT , 求窗口内的个数
//                @Override
//                public Integer createAccumulator() {
//                    return 0;
//                }
//
//                @Override
//                public Integer add(SensorReading value, Integer accumulator) {
//                    return accumulator + 1;
//                }
//
//                @Override
//                public Integer getResult(Integer accumulator) {
//                    return accumulator;
//                }
//
//                @Override
//                public Integer merge(Integer a, Integer b) { // 主要在SessionWindow,其他主要用不到合并
//                    return a + b;
//                }
//
//            }).print();


        // TODO 2.全量聚合窗口 WindowFunction / ProcessWindowFunction 后者更全面
        sensorStream
            .keyBy(SensorReading::getId)
            .window(TumblingProcessingTimeWindows.of(Time.seconds(15)))
            .process(new ProcessWindowFunction<SensorReading, Tuple3<String,Long,Integer>, String, TimeWindow>() { //<IN, OUT, KEY, W extends Window>
                @Override
                public void process(String s, Context context, Iterable<SensorReading> elements, Collector<Tuple3<String,Long,Integer>> out) throws Exception {
                    String id = s;
                    long windowEnd = context.window().getEnd();
                    int count = IteratorUtils.toList(elements.iterator()).size();
                    out.collect(Tuple3.of(id,windowEnd,count));
                }
            })
            .print();

//             使用 apply(windowFunction)
//             apply不提供聚合方法
//            .apply(new WindowFunction<SensorReading, Tuple3<String, Long, Integer>, String, TimeWindow>() { //<IN, OUT, KEY, W extends Window>
//                // apply(KEY key, W window, Iterable<IN> input, Collector<OUT> out
//                @Override
//                public void apply(String s, TimeWindow window, Iterable<SensorReading> input, Collector<Tuple3<String, Long, Integer>> out) throws Exception {
//                    String id = s;
//                    long windowEnd = window.getEnd();
//                    int count = IteratorUtils.toList(input.iterator()).size();
//                    out.collect(Tuple3.of(id,windowEnd,count));
//                }
//            })
//            .print("result2");

        env.execute();
    }
}
