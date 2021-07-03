package com.atguigu.apitest.timer;

import com.atguigu.apitest.beans.SensorReading;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

import java.time.Duration;

/**
 * 7.7.1基于事件时间的定时器
 * 在测试的时候, 脑子里面要想着: 时间进展依据的是watermark
 */
public class TimeTest2_EventTime {
    public static void main(String[] args) throws  Exception{
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        // 从文件读取数据
        SingleOutputStreamOperator<SensorReading> dataStream =env
            .socketTextStream("localhost", 7777)  // 在socket终端只输入毫秒级别的时间戳
            .map(new RichMapFunction<String, SensorReading>() {
                @Override
                public SensorReading map(String value) throws Exception {
                    String[] arr = value.split(",");
                    return new SensorReading(arr[0],Long.parseLong(arr[1]),Double.parseDouble(arr[2]));
                }
            });

        //定义watermark
        WatermarkStrategy<SensorReading> wms = WatermarkStrategy
            .<SensorReading>forBoundedOutOfOrderness(Duration.ofSeconds(3))
            .withTimestampAssigner(new SerializableTimestampAssigner<SensorReading>(){

                @Override
                public long extractTimestamp(SensorReading element, long recordTimestamp) {
                    return element.getTimestamp() * 1000L;
                }
            });
        
        dataStream
            .assignTimestampsAndWatermarks(wms)
            .keyBy(SensorReading::getId)
            .process(new KeyedProcessFunction<String, SensorReading, String>() {
                @Override
                public void processElement(SensorReading value, Context ctx, Collector<String> out) throws Exception {
                    ctx.timerService().registerEventTimeTimer(ctx.timestamp() + 5000);
                    out.collect(value.toString());
                }

                @Override
                public void onTimer(long timestamp, OnTimerContext ctx, Collector<String> out) throws Exception {
                    System.out.println(timestamp);
                    System.out.println("定时器被出发了....");
                }
            })
            .print();




        env.execute();
    }

}
