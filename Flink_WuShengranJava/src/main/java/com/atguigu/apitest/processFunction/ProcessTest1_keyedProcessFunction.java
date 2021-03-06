package com.atguigu.apitest.processFunction;

import com.atguigu.apitest.beans.SensorReading;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

/**
 * Created by John.Ma on 2021/7/5 0005 0:58
 *
 * 设置一个获取数据后第5s给出提示信息的定时器。
 */
public class ProcessTest1_keyedProcessFunction {
    public static void main(String[] args) throws Exception{
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        // socket文本流
        DataStreamSource<String> inputStream = env.socketTextStream("localhost", 7777);

        // 转换成SensorReading类型
        DataStream<SensorReading> dataStream = inputStream.map(line -> {
            String[] fields = line.split(",");
            return new SensorReading(fields[0], new Long(fields[1]), new Double(fields[2]));
        });

        // 测试KeyedProcessFunction,先分组然后自定义处理
        dataStream
            .keyBy(SensorReading::getId)
            .process(new MyProcess())
            .print();

        env.execute();

    }

    public static class MyProcess extends KeyedProcessFunction<String,SensorReading,Integer>{ // k,i,o

        private ValueState<Long> tsTimerState ;

        @Override
        public void open(Configuration parameters) throws Exception {
            tsTimerState = getRuntimeContext().getState(new ValueStateDescriptor<Long>("ts-timer",Long.class));
        }

        @Override
        public void processElement(SensorReading value, Context ctx, Collector<Integer> out) throws Exception {
            out.collect(value.getId().length());


            // context的用法
            ctx.timestamp();  // 获取当前时间戳,事件时间
            ctx.getCurrentKey(); // 当前key
//            ctx.output(); 侧输出流
            ctx.timerService().currentProcessingTime();
            ctx.timerService().currentWatermark();

            // 在5处理时间的5秒延迟后触发
            System.out.println("当前时间: "+ctx.timerService().currentProcessingTime());
            ctx.timerService().registerProcessingTimeTimer(ctx.timerService().currentProcessingTime()+5000L);
            tsTimerState.update(ctx.timerService().currentProcessingTime()+1000L);
            //            ctx.timerService().registerEventTimeTimer((value.getTimestamp() + 10) * 1000L);
            // 删除指定时间触发的定时器
            //            ctx.timerService().deleteProcessingTimeTimer(tsTimerState.value());

        }

        @Override
        public void onTimer(long timestamp, OnTimerContext ctx, Collector<Integer> out) throws Exception {
            System.out.println(timestamp+" 定时器触发");
            ctx.getCurrentKey();
//            ctx.output(); 侧输出流
            ctx.timeDomain()// 时间域
            ;
        }

        @Override
        public void close() throws Exception {
            tsTimerState.clear();
        }
    }
}
