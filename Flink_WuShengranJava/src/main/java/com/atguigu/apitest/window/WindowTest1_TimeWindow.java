package com.atguigu.apitest.window;

import com.atguigu.apitest.beans.SensorReading;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.WindowAssigner;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.triggers.Trigger;
import org.apache.flink.streaming.api.windowing.windows.Window;

import java.util.Collection;

/**
 * @description:
 * @author: malichun
 * @time: 2021/7/2/0002 11:23
 */
public class WindowTest1_TimeWindow {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        //执行环境并行度设置为1
//        env.setParallelism(1);
        //从文件中获取数据输出
//        DataStream<String> inputStream = env.readTextFile("E:\\gitdir\\learn_projects\\myLearn\\Flink_WuShengranJava\\src\\main\\resources\\sensor.txt");
        DataStream<String> inputStream = env.socketTextStream("www.bigdata01.com",7777);

        DataStream<SensorReading> sensorStream = inputStream.map(s -> {
            String[] fields = s.split(",");
            return new SensorReading(fields[0], new Long(fields[1]), new Double(fields[2]));
        });

        sensorStream.keyBy(SensorReading::getId)
                .window(TumblingProcessingTimeWindows.of(Time.seconds(5))) // 添加滚动窗口
                .max("temperature")
                .print();


        env.execute();
    }
}
