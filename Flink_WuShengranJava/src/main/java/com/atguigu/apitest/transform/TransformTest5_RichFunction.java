package com.atguigu.apitest.transform;

import com.atguigu.apitest.beans.SensorReading;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

/**
 * Created by John.Ma on 2021/6/29 0029 23:51
 */
public class TransformTest5_RichFunction {
    public static void main(String[] args) throws Exception {
        //创建执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        //执行环境并行度设置为1
//        env.setParallelism(1);
        //从文件中获取数据输出
        DataStream<String> dataStream = env.readTextFile("E:\\gitdir\\learn_projects\\myLearn\\Flink_WuShengranJava\\src\\main\\resources\\sensor.txt");

        DataStream<SensorReading> sensorStream = dataStream.map(s -> {
            String[] fields = s.split(",");
            return new SensorReading(fields[0],new Long(fields[1]),new Double(fields[2]));
        });

        SingleOutputStreamOperator<Tuple2<String, Integer>> resultStream = sensorStream.map(new MyMapper());

//        resultStream.print();

        resultStream.addSink(new RichSinkFunction<Tuple2<String, Integer>>() {

            @Override
            public void invoke(Tuple2<String, Integer> value, Context context) throws Exception {
                System.out.println(getRuntimeContext().getIndexOfThisSubtask() +">"+ value);
            }
        });
        env.execute();


    }
    // 传统的Function 不能获取上下文信息,只能处理当前数据,不能和其他数据交互
    public static class MyMapper0 implements MapFunction<SensorReading, Tuple2<String,Integer>>{

        @Override
        public Tuple2<String, Integer> map(SensorReading value) throws Exception {
            return new Tuple2<>(value.getId(),value.getId().length());
        }
    }

    //实现自定义富函数类(RichMapFunction是一个抽象类)
    public static class MyMapper extends RichMapFunction<SensorReading,Tuple2<String,Integer>>{
        @Override
        public void open(Configuration parameters) throws Exception {
            // 初始化工作,一般是定义状态,或者建立数据库连接
            System.out.println("open");
        }

        @Override
        public Tuple2<String, Integer> map(SensorReading value) throws Exception {
//            getRuntimeContext().getState(new ValueStateDescriptor<Object>())
            return Tuple2.of(value.getId(),getRuntimeContext().getIndexOfThisSubtask());
        }

        @Override
        public void close() throws Exception {
            // 一般是关闭和清空状态的收尾工作
            System.out.println("close");
        }
    }

}
