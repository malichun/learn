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
 * 需求:监控水位传感器的水位值，如果水位值在五分钟之内(processing time)连续上升，则报警。
 */
public class TimeTest3_Demo {
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

        //  TODO 如果温度值在五分钟之内(processing time)连续上升，则报警。

        dataStream
            .assignTimestampsAndWatermarks(wms)
            .keyBy(SensorReading::getId)
            .process(new KeyedProcessFunction<String, SensorReading, String>() {
                // 后面用状态代替
                double lastTemperature =0.0;
                long timerTs = Long.MIN_VALUE;

                @Override
                public void processElement(SensorReading value, Context ctx, Collector<String> out) throws Exception {
                    if(value.getTemperature() > lastTemperature){
                        if(timerTs == Long.MIN_VALUE){
                            System.out.println("注册......");
                            timerTs = ctx.timestamp() + 5000L;
                            ctx.timerService().registerEventTimeTimer(timerTs);
                        }
                    }else{
                        ctx.timerService().deleteEventTimeTimer(timerTs);
                        timerTs = Long.MIN_VALUE;
                    }
                    lastTemperature = value.getTemperature();
                    System.out.println("最新的一次温度"+lastTemperature);
                }

                @Override
                public void onTimer(long timestamp, OnTimerContext ctx, Collector<String> out) throws Exception {
                    out.collect(ctx.getCurrentKey() + "报警!!!");
                    timerTs = Long.MIN_VALUE;
                }
            })
            .print();




        env.execute();
    }

}
