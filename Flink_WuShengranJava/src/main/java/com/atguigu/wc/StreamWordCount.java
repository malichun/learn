package com.atguigu.wc;

import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

/**
 * Created by John.Ma on 2021/6/26 0026 23:43
 */
public class StreamWordCount {
    public static void main(String[] args) throws Exception {
        ParameterTool pt = ParameterTool.fromArgs(args);
        String host = pt.get("host");
        int port = pt.getInt("port");

        //创建流处理执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
//        env.setParallelism()
        // 从文件里面读取
        // 从文件中读取器数据
//        DataStream<String> inputStream = env.readTextFile("D:\\fileImportant\\Learn_projects\\learn\\Flink_WuShengranJava\\src\\main\\resources\\hello.txt");

        DataStream<String> inputStream = env.socketTextStream(host, port).slotSharingGroup("default"); // 设置共享组
        DataStream<Tuple2<String, Integer>> resultStream = inputStream.flatMap(new RichFlatMapFunction<String, Tuple2<String, Integer>>() {
            @Override
            public void flatMap(String value, Collector<Tuple2<String, Integer>> out) throws Exception {
                String[] arr = value.split(" ");
                for (String s1 : arr) {
                    out.collect(Tuple2.of(s1, 1));
                }
            }
        }).keyBy(tuple2 -> tuple2.f0)
                .sum(1).setParallelism(2);

        resultStream
                .print();


        env.execute();


    }
}
