package com.atguigu.apitest.processFunction;

import com.atguigu.apitest.beans.SensorReading;
import org.apache.flink.api.common.functions.JoinFunction;
import org.apache.flink.streaming.api.datastream.JoinedStreams;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;

/**
 * nc -L -p 7777
 * nc -L -p 8888
 */
public class ProcessJoinFunctionTest {
    public static void main(String[] args) throws Exception{
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        SingleOutputStreamOperator<SensorReading> s1 = env.socketTextStream("localhost", 7777)//在socket终端只输入毫秒级别的时间戳
        .map(value -> {
            String[] datas = value.split(",");
            return new SensorReading(datas[0], Long.valueOf(datas[1]), Double.valueOf(datas[2]));
        });

        SingleOutputStreamOperator<SensorReading> s2 = env.socketTextStream("localhost", 8888)//在socket终端只输入毫秒级别的时间戳
            .map(value -> {
                String[] datas = value.split(",");
                return new SensorReading(datas[0], Long.valueOf(datas[1]), Double.valueOf(datas[2]));
            });

        JoinedStreams<SensorReading, SensorReading> joinedStreams = s1.join(s2);

        joinedStreams
            .where(SensorReading::getId)
            .equalTo(SensorReading::getId)
            .window(TumblingProcessingTimeWindows.of(Time.seconds(5)))  // 必须使用窗口
            .apply(new JoinFunction<SensorReading, SensorReading, String>() { //<IN1, IN2, OUT>
                @Override
                public String join(SensorReading first, SensorReading second) throws Exception {
                    return "first: " + first + ", second: " + second;
                }
            })
            .print();

        env.execute();
    }
}
