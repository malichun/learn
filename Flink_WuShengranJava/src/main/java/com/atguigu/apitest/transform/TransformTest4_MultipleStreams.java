package com.atguigu.apitest.transform;

import org.apache.flink.streaming.api.datastream.ConnectedStreams;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.RichCoFlatMapFunction;
import org.apache.flink.util.Collector;

import java.util.Arrays;

/**
 * Connect ,coMap coFlatMap
 */
public class TransformTest4_MultipleStreams {
    public static void main(String[] args) throws Exception {
        //创建执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        //执行环境并行度设置为1
//        env.setParallelism(1);
        //从文件中获取数据输出
        DataStream<Integer> stream1 = env.fromCollection(Arrays.asList(1,2,3,4,5));
        DataStream<String> stream2 = env.fromCollection(Arrays.asList("a","b","c","d","e"));

        ConnectedStreams<Integer, String> connectedStreams = stream1.connect(stream2);

        connectedStreams.flatMap(new RichCoFlatMapFunction<Integer, String, Object>() {
            @Override
            public void flatMap1(Integer value, Collector<Object> out) throws Exception {
                out.collect(value);
            }

            @Override
            public void flatMap2(String value, Collector<Object> out) throws Exception {
                out.collect(value);
            }
        }).print();

        env.execute();
    }

}
