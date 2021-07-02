package com.atguigu.apitest.transform;

import com.atguigu.apitest.beans.SensorReading;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * Created by John.Ma on 2021/6/29 0029 23:19
 */
public class TransformTest2_Reduce {
    public static void main(String[] args) throws Exception {
        //创建执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        //执行环境并行度设置为1
        env.setParallelism(1);
        //从文件中获取数据输出
        DataStream<String> dataStream = env.readTextFile("D:\\fileImportant\\Learn_projects\\learn\\Flink_WuShengranJava\\src\\main\\resources\\sensor.txt");

        DataStream<SensorReading> sensorStream = dataStream.map(s -> {
            String[] fields = s.split(",");
            return new SensorReading(fields[0],new Long(fields[1]),new Double(fields[2]));
        });

        // 先分组,再聚合
        KeyedStream<SensorReading, String> keyedStream = sensorStream.keyBy(SensorReading::getId);

        //reduce ,自定义归约函数,获取max温度的传感器信息以外,时间戳更新成最新的
        DataStream<SensorReading> resultStream = keyedStream.reduce(new ReduceFunction<SensorReading>() {
            @Override
            public SensorReading reduce(SensorReading value1, SensorReading value2) throws Exception {
                double maxTemp = Math.max(value1.getTemperature(),value2.getTemperature());
                return new SensorReading(value1.getId(),value2.getTimestamp(),maxTemp);
            }
        });


        resultStream.print("result");
        env.execute();

    }
}
