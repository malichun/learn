package com.atguigu.apitest.window;

import com.atguigu.apitest.beans.SensorReading;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.time.Duration;

/**
 * Created by John.Ma on 2021/7/3 0003 0:06
 */
public class WindowTest3_EventTimeWindow {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        //1.12版本默认是事件时间
//        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        DataStream<String> inputStream = env.readTextFile("D:\\fileImportant\\Learn_projects\\learn\\Flink_WuShengranJava\\src\\main\\resources\\sensor.txt");
//        DataStream<String> inputStream = env.socketTextStream("www.bigdata01.com", 7777);

        DataStream<SensorReading> dataStream = inputStream.map(s -> {
            String[] fields = s.split(",");
            return new SensorReading(fields[0], new Long(fields[1]), new Double(fields[2]));
        });

        // 创建水印生产策略
        WatermarkStrategy<SensorReading> wms = WatermarkStrategy
                .<SensorReading>forBoundedOutOfOrderness(Duration.ofSeconds(3)) // 最大的容忍的延迟时间
                .withTimestampAssigner(new SerializableTimestampAssigner<SensorReading>(){
                    @Override
                    public long extractTimestamp(SensorReading sensorReading, long l) {
                        return sensorReading.getTimestamp() * 1000L ;
                    }
                });


        dataStream
                .assignTimestampsAndWatermarks(wms) // 指定水印和时间戳
                .keyBy(SensorReading::getId)
                .window(TumblingEventTimeWindows.of(Time.seconds(5)))
                .process(new ProcessWindowFunction<SensorReading, String, String, TimeWindow>() { // <IN, OUT, KEY, W extends Window>
                    @Override
                    public void process(String s, Context context, Iterable<SensorReading> elements, Collector<String> out) throws Exception {
                        String msg = "当前key: "+s
                                +  "窗口 :[" + context.window().getStart()/1000 + ","
                                + context.window().getEnd()/ 1000 + ")一共有"
                                + elements.spliterator().estimateSize() + "条数据 ";
                        out.collect(msg);
                    }
                })
                .print();

        env.execute();


    }
}
