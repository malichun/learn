package com.atguigu.apitest.state;

import com.atguigu.apitest.beans.SensorReading;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.runtime.state.FunctionInitializationContext;
import org.apache.flink.runtime.state.FunctionSnapshotContext;
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;


/**
 * 在map算子中计算数据的个数
 *
 * socket中输入:
 * sensor_1,1547718199,35.8
 * sensor_1,1547718199,35.8
 * sensor_1,1547718199,35.8
 * sensor_1,1547718199,35.8
 * sensor_1,1547718199,35.8
 *
 * 结果:
 *    initializeState...
 *    initializeState...
 *    initializeState...
 *    1> 1
 *    2> 1
 *    3> 1
 *    1> 2
 *    2> 2
 *
 */
public class StateTest1_OperatorState1_ListState {
    public static void main(String[] args) throws  Exception{
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(3);

        // socket文本流
        DataStreamSource<String> inputStream = env.socketTextStream("localhost", 7777);

        // 转换成SensorReading类型
        DataStream<SensorReading> dataStream = inputStream.map(line -> {
            String[] fields = line.split(",");
            return new SensorReading(fields[0], new Long(fields[1]), new Double(fields[2]));
        });

        // 定义一个有状态的map操作,统计当前分区数据个数
        SingleOutputStreamOperator<Long> resultStream = dataStream.map(new MyCountMapper());

        resultStream.print();
        env.execute();
    }

    public  static class MyCountMapper implements MapFunction<SensorReading,Long>, CheckpointedFunction {
        private Long count = 0L;
        private ListState<Long> state;

        @Override
        public Long map(SensorReading value) throws Exception {
            count++;
            return count;
        }

        // 初始化的时候会调用这个方法,向本地状态中填充数据 每个子任务调用一次
        @Override
        public void initializeState(FunctionInitializationContext context) throws Exception {
            System.out.println("initializeState...");
            state = context
                .getOperatorStateStore()
                .getListState(new ListStateDescriptor<Long>("state",Long.class));

            for(Long c: state.get()){
                count += c;
            }
        }

        // CheckPoint时会调用这个方法,我们要实现具体的snapshot逻辑,比如将哪些本地状态持久化
        @Override
        public void snapshotState(FunctionSnapshotContext context) throws Exception {
            System.out.println("snapshotState...");
            state.clear();
            state.add(count);
        }
    }
}
