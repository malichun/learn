package com.atguigu.apitest.state;

import com.atguigu.apitest.beans.SensorReading;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.common.state.AggregatingState;
import org.apache.flink.api.common.state.AggregatingStateDescriptor;
import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

/**
 * Created by John.Ma on 2021/7/4 0004 14:16
 *
 * 去重: 去掉重复的水位值. 思路: 把水位值作为MapState的key来实现去重, value随意
 */
public class StateTest7_KeyedState5_MapState {
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

                private MapState<Double,String> mapState;
                @Override
                public void open(Configuration parameters) throws Exception {
                    mapState =  getRuntimeContext().getMapState(new MapStateDescriptor<Double, String>("mapState",Double.class,String.class));
                }

                @Override
                public void processElement(SensorReading value, Context ctx, Collector<Double> out) throws Exception {
                    if(!mapState.contains(value.getTemperature())){
                        out.collect(value.getTemperature());
                        mapState.put(value.getTemperature(),"随意");
                    }
                }
            })
            .print();

        env.execute();
    }
}
