package com.atguigu.apitest.state;

import com.atguigu.apitest.beans.SensorReading;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.common.state.AggregatingState;
import org.apache.flink.api.common.state.AggregatingStateDescriptor;
import org.apache.flink.api.common.state.ReducingState;
import org.apache.flink.api.common.state.ReducingStateDescriptor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

/**
 * Created by John.Ma on 2021/7/4 0004 14:16
 *
 * 计算每个传感器的平均水位
 */
public class StateTest6_KeyedState4_AggregatingState {
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
                private AggregatingState<SensorReading,Double> aggregatingState;

                @Override
                public void open(Configuration parameters) throws Exception {
                    //<IN, ACC, OUT>
                    aggregatingState= getRuntimeContext()
                        //String name,
                        //			AggregateFunction<IN, ACC, OUT> aggFunction,
                        //			Class<ACC> stateType
                        .getAggregatingState(new AggregatingStateDescriptor<SensorReading, Tuple2<Double,Long>, Double>("avg", new AggregateFunction<SensorReading, Tuple2<Double, Long>, Double>() {
                            @Override
                            public Tuple2<Double, Long> createAccumulator() {
                                return Tuple2.of(0.0,0L);
                            }

                            @Override
                            public Tuple2<Double, Long> add(SensorReading value, Tuple2<Double, Long> accumulator) {
                                return Tuple2.of(accumulator.f0 + value.getTemperature(), accumulator.f1 + 1);
                            }

                            @Override
                            public Double getResult(Tuple2<Double, Long> accumulator) {
                                return accumulator.f0 / accumulator.f1;
                            }

                            @Override
                            public Tuple2<Double, Long> merge(Tuple2<Double, Long> a, Tuple2<Double, Long> b) {
                                return Tuple2.of(a.f0 + b.f0, a.f1+ b.f1);
                            }
                        }, Types.TUPLE(Types.DOUBLE,Types.LONG)));
                }

                @Override
                public void processElement(SensorReading value, Context ctx, Collector<Double> out) throws Exception {
                    aggregatingState.add(value);
                    out.collect(aggregatingState.get());
                }
            })
            .print();

        env.execute();
    }
}
