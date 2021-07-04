package com.atguigu.apitest.state;

import com.atguigu.apitest.beans.SensorReading;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.state.*;
import org.apache.flink.runtime.state.FunctionInitializationContext;
import org.apache.flink.runtime.state.FunctionSnapshotContext;
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction;
import org.apache.flink.streaming.api.datastream.BroadcastStream;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.BroadcastProcessFunction;
import org.apache.flink.util.Collector;


/**
 * 从版本1.5.0开始，Apache Flink具有一种新的状态，称为广播状态。
 * 	广播状态被引入以支持这样的用例:来自一个流的一些数据需要广播到所有下游任务，在那里它被本地存储，并用于处理另一个流上的所有传入元素。
 * 	作为广播状态自然适合出现的一个例子，
 *
 * 	我们可以想象一个低吞吐量流，其中包含一组规则，我们希望根据来自另一个流的所有元素对这些规则进行评估。
 * 	考虑到上述类型的用例，广播状态与其他操作符状态的区别在于:
 * 1. 它是一个map格式。
 * 2. 它只对输入有广播流和无广播流的特定操作符可用。
 * 3. 这样的操作符可以具有具有不同名称的多个广播状态。
 *
 */
public class StateTest2_OperatorState2_BroadCastState {
    public static void main(String[] args) throws  Exception{
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(3);

        // socket文本流
        DataStreamSource<String> dataStream = env.socketTextStream("localhost", 7777);
        DataStreamSource<String> controlStream = env.socketTextStream("localhost", 8888);

        MapStateDescriptor<String,String> stateDescriptor = new MapStateDescriptor<String, String>("state",String.class,String.class);
        // 广播流
        BroadcastStream<String> broadcastStream = controlStream.broadcast(stateDescriptor);

        dataStream
            .connect(broadcastStream)
            .process(new BroadcastProcessFunction<String, String, String>() { // IN1,IN2,OUT

                @Override
                public void processElement(String value, ReadOnlyContext ctx, Collector<String> out) throws Exception {
                    // 从广播状态中取值,不同的值做不同的业务
                    ReadOnlyBroadcastState<String, String> state = ctx.getBroadcastState(stateDescriptor);
                    if("1".equals(state.get("switch"))){
                        out.collect("切换到1号配置...");
                    }else if("0".equals(state.get("switch"))){
                        out.collect("切换到0号配置...");
                    }else{
                        out.collect("切换到其他配置...");
                    }
                }

                @Override
                public void processBroadcastElement(String value, Context ctx, Collector<String> out) throws Exception {
                    BroadcastState<String, String> broadcastState = ctx.getBroadcastState(stateDescriptor);
                    // 把值放入广播状态
                    broadcastState.put("switch",value);
                }

            })
            .print();

        env.execute();
    }

}
