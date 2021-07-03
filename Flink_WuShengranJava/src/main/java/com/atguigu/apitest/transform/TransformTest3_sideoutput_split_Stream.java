package com.atguigu.apitest.transform;

import com.atguigu.apitest.beans.SensorReading;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

/**
 * Created by John.Ma on 2021/7/3 0003 23:48
 */
public class TransformTest3_sideoutput_split_Stream {
    public static void main(String[] args) throws Exception {
        //创建执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        //执行环境并行度设置为1
        env.setParallelism(1);
        //从文件中获取数据输出
        DataStream<String> dataStream = env.readTextFile("D:\\fileImportant\\Learn_projects\\learn\\Flink_WuShengranJava\\src\\main\\resources\\sensor.txt");

        DataStream<SensorReading> sensorStream = dataStream.map(s -> {
            String[] fields = s.split(",");
            return new SensorReading(fields[0],new Long(fields[1]),new Double(fields[2]));
        });
        SingleOutputStreamOperator<SensorReading> result = sensorStream
            .keyBy(SensorReading::getId)
            .process(new KeyedProcessFunction<String, SensorReading, SensorReading>() {//<K, I, O>
                @Override
                public void processElement(SensorReading value, Context ctx, Collector<SensorReading> out) throws Exception {
                    if(value.getTemperature()> 30.0){
                        ctx.output(new OutputTag<SensorReading>("警告"){},value);
                    }else{
                        out.collect(value);
                    }
                }
            });

        result.print("主流");
        result.getSideOutput(new OutputTag<SensorReading>("警告"){}).print("警告");

        env.execute();

    }
}
