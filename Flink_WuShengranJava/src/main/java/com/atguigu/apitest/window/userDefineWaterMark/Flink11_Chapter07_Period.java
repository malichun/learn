package com.atguigu.apitest.window.userDefineWaterMark;

import com.atguigu.apitest.beans.SensorReading;
import org.apache.flink.api.common.eventtime.*;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

/**
 * 自定义watermark
 * 1. 周期性 自定义watermark
 */
public class Flink11_Chapter07_Period {
    public static void main(String[] args) throws Exception{
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        SingleOutputStreamOperator<SensorReading> stream = env
            .socketTextStream("hadoop102", 9999)  // 在socket终端只输入毫秒级别的时间戳
            .map(new MapFunction<String, SensorReading>() {
                @Override
                public SensorReading map(String value) throws Exception {
                    String[] datas = value.split(",");
                    return new SensorReading(datas[0], Long.valueOf(datas[1]), Double.valueOf(datas[2]));

                }
            });

        // 创建水印产生策略
        WatermarkStrategy<SensorReading> myWms = new WatermarkStrategy<SensorReading>() {
            @Override
            public WatermarkGenerator<SensorReading> createWatermarkGenerator(WatermarkGeneratorSupplier.Context context) {
                System.out.println("createWatermarkGenerator....");
                return new MyPeriod(3);
            }
        };

        stream
            .assignTimestampsAndWatermarks(myWms)
            .keyBy(SensorReading::getId)
            .window(SlidingEventTimeWindows.of(Time.seconds(5),Time.seconds(5)))
            .process(new ProcessWindowFunction<SensorReading, String, String, TimeWindow>() {
                @Override
                public void process(String key, Context context, Iterable<SensorReading> elements, Collector<String> out) throws Exception {
                    String msg = "当前key: "+ key
                        + "窗口: ["+context.window().getStart()/ 1000 + "," +
                        context.window().getEnd()/ 1000+") 一共有"
                        +elements.spliterator().estimateSize() + " 条数据";
                        out.collect(context.window().toString());
                        out.collect(msg);
                }
            })
            .print();

        env.execute();
    }

    public static class MyPeriod implements WatermarkGenerator<SensorReading>{

        private long maxTs = Long.MIN_VALUE;
        //允许的最大延迟时间
        private final long maxDelay;

        public MyPeriod(long maxDelay) {
            this.maxDelay = maxDelay;
            this.maxTs = Long.MIN_VALUE + this.maxDelay + 1;
        }

        // 每收到一个元素,执行一次,用来产生Watermark中的时间戳
        @Override
        public void onEvent(SensorReading event, long eventTimestamp, WatermarkOutput output) {
            System.out.println("onEvent..."+ eventTimestamp);
            maxTs = Math.max(maxTs,eventTimestamp);
            System.out.println("maxTs");
        }

        // 周期性的把Watermark发射出去,默认周期是200ms
        @Override
        public void onPeriodicEmit(WatermarkOutput output) {
            System.out.println("onPeriodicEmit...");
            // 周期性的发射水印,相当于Flink把自己的时钟周期满了一个最大延迟
            output.emitWatermark(new Watermark(maxTs - maxDelay -1 ));
        }
    }

}
