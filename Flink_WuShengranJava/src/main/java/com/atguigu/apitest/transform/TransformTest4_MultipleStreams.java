package com.atguigu.apitest.transform;

import com.atguigu.apitest.beans.SensorReading;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * Created by John.Ma on 2021/6/29 0029 23:42
 */
public class TransformTest4_MultipleStreams {
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



    }

}
