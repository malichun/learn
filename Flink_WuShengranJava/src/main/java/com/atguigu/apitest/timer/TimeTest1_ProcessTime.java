package com.atguigu.apitest.timer;

import com.atguigu.apitest.beans.SensorReading;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

/**
 * 7.7.1基于处理时间的定时器
 */
public class TimeTest1_ProcessTime {
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

        dataStream
            .keyBy(SensorReading::getId)
            .process(new KeyedProcessFunction<String, SensorReading, String>() { // K,I,O
                @Override
                public void processElement(SensorReading value, Context ctx, Collector<String> out) throws Exception {
                    // 初始时间过5s后出发定时器
                    ctx.timerService().registerProcessingTimeTimer(ctx.timerService().currentProcessingTime() + 5000);
                    out.collect(value.toString());
                }

                // 定时器被触发后,回调这个方法
                // 参数1: 触发器被出发的时间
                @Override
                public void onTimer(long timestamp, OnTimerContext ctx, Collector<String> out) throws Exception {
                    System.out.println(timestamp);
                    out.collect("我被出发了...");
                }
            })
            .print();



        env.execute();
    }

}
