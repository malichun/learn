package com.atguigu.apitest.state;

import com.atguigu.apitest.beans.SensorReading;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/**
 * Created by John.Ma on 2021/7/4 0004 14:16
 *
 * 针对每个传感器输出最高的3个水位值
 *
 */
public class StateTest4_KeyedState2_ListState {
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
            .process(new KeyedProcessFunction<String, SensorReading, List<SensorReading>>() {

                private ListState<SensorReading> listState;

                @Override
                public void open(Configuration parameters) throws Exception {
                    listState = getRuntimeContext().getListState(new ListStateDescriptor<SensorReading>("top3",SensorReading.class));
                }

                @Override
                public void processElement(SensorReading value, Context ctx, Collector<List<SensorReading>> out) throws Exception {
                    listState.add(value);
                    // 1. 获取状态中所有水位高度,并排序
                    List<SensorReading> list = new ArrayList<>();
                    listState.get().forEach(list::add);
                    // 2. 降序排序
                    list.sort((s1,s2) -> -(int) (s1.getTemperature() - s2.getTemperature()));
                    // 3.当长度超过3的时候移除最后一个
                    if(list.size()> 3){
                        list.remove(3);
                    }
                    listState.update(list);
                    out.collect(list);
                }
            })
            .print();
        env.execute();
    }
}
