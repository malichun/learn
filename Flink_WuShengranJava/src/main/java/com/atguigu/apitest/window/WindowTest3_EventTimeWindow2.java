package com.atguigu.apitest.window;

import com.atguigu.apitest.beans.SensorReading;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

import java.sql.PreparedStatement;
import java.time.Duration;

/**
 * Created by John.Ma on 2021/7/3 0003 0:06
 */
public class WindowTest3_EventTimeWindow2 {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
//        env.setParallelism(1);
        env.setParallelism(4);
//        env.getConfig().setAutoWatermarkInterval(100);//设置watermark生成间隔时间

        // 创建水印生产策略
        WatermarkStrategy<SensorReading> wms = WatermarkStrategy
            .<SensorReading>forBoundedOutOfOrderness(Duration.ofSeconds(2)) // 最大的容忍的延迟时间
            .withTimestampAssigner(new SerializableTimestampAssigner<SensorReading>() {
                @Override
                public long extractTimestamp(SensorReading sensorReading, long l) {
                    return sensorReading.getTimestamp() * 1000L;
                }
            });


        //1.12版本默认是事件时间
//        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
//        DataStream<String> inputStream = env.readTextFile("D:\\fileImportant\\Learn_projects\\learn\\Flink_WuShengranJava\\src\\main\\resources\\sensor.txt");
        DataStream<String> inputStream = env.socketTextStream("www.bigdata01.com", 7777);

        DataStream<SensorReading> dataStream = inputStream.map(s -> {
            String[] fields = s.split(",");
            return new SensorReading(fields[0], new Long(fields[1]), new Double(fields[2]));
        }).assignTimestampsAndWatermarks(wms);

        OutputTag<SensorReading> outputTag = new OutputTag<SensorReading>("late"){};// 为什么需要加上{}

        // 基于事件时间的开窗聚合，统计15秒内温度的最小值
        SingleOutputStreamOperator<SensorReading> minTemStream = dataStream
            .keyBy(SensorReading::getId)
            .window(TumblingEventTimeWindows.of(Time.seconds(15)))
            .allowedLateness(Time.minutes(1))
            .sideOutputLateData(outputTag)
            .minBy("temperature");

        minTemStream.print("minTemp");
        minTemStream.getSideOutput(outputTag).print("late");


        env.execute();


    }
}
