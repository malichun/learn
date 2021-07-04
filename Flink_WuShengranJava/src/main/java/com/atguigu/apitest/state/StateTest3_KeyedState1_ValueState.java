package com.atguigu.apitest.state;

import com.atguigu.apitest.beans.SensorReading;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

/**
 * Created by John.Ma on 2021/7/4 0004 14:16
 *
 * 检测传感器的水位线值，如果连续的两个水位线差值超过10，就输出报警。
 */
public class StateTest3_KeyedState1_ValueState {
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
            .process(new KeyedProcessFunction<String, SensorReading, String>() {
                private ValueState<Double> state;

                @Override
                public void open(Configuration parameters) throws Exception {
                    state = getRuntimeContext().getState(new ValueStateDescriptor<Double>("state",Double.class));
                }

                @Override
                public void processElement(SensorReading value, Context ctx, Collector<String> out) throws Exception {
                    double lastVc = state.value() == null ? 0 : state.value();
                    if(Math.abs(value.getTemperature() - lastVc) >= 10){
                        out.collect(value.getId() +" 红色报警!!!");
                    }
                    state.update(value.getTemperature());
                }
            })
            .print();

        env.execute();
    }
}
