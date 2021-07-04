package com.atguigu.apitest.state;

import com.atguigu.apitest.beans.SensorReading;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.state.ReducingState;
import org.apache.flink.api.common.state.ReducingStateDescriptor;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by John.Ma on 2021/7/4 0004 14:16
 *
 * 计算每个传感器的水位和
 */
public class StateTest5_KeyedState3_ReducingState {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment
            .getExecutionEnvironment()
            .setParallelism(3);

        env
            .socketTextStream("localhost",7777)
            .map(value  -> {
                String[] datas = value.split(",");
                return new SensorReading(datas[0], Long.valueOf(datas[1]), Double.valueOf(datas[2]));
            })
            .keyBy(SensorReading::getId)
            .process(new KeyedProcessFunction<String, SensorReading, Double>() {

                private ReducingState<Double> readingReducingState;

                @Override
                public void open(Configuration parameters) throws Exception {
                    readingReducingState = getRuntimeContext().getReducingState(new ReducingStateDescriptor<Double>("top3", Double::sum, Double.class));
                }

                @Override
                public void processElement(SensorReading value, Context ctx, Collector<Double> out) throws Exception {
                    readingReducingState.add(value.getTemperature());
                    out.collect(readingReducingState.get());
                }
            })
            .print();

        env.execute();
    }
}
