package com.atguigu.apitest.transform;

import com.atguigu.apitest.beans.SensorReading;
import jdk.internal.util.xml.impl.Input;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.io.InputStream;

/**
 * Created by John.Ma on 2021/6/30 0030 1:03
 */
public class TransformTest6_Partition {
    public static void main(String[] args) throws Exception {
        //创建执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        //执行环境并行度设置为4
        env.setParallelism(4);

        //从文件中获取数据输出
        DataStream<String> inputStream = env.readTextFile("D:\\fileImportant\\Learn_projects\\learn\\Flink_WuShengranJava\\src\\main\\resources\\sensor.txt");

        DataStream<SensorReading> dataStream   = inputStream.map(s -> {
            String[] fields = s.split(",");
            return new SensorReading(fields[0], new Long(fields[1]), new Double(fields[2]));
        });

        // SingleOutputStreamOperator多并行度默认就rebalance,轮询方式分配
        dataStream.print("input");

        //1.shuffle(并非批处理中获取一批才打乱,这里每次获取到直接打乱且分区)
        DataStream<String> shuffleStream = inputStream.shuffle();
        shuffleStream.print("shuffle");

        // 2. keyBy (hash 然后取模)
        dataStream.keyBy(SensorReading::getId).print("keyBy");

        //3. global(直接发送给第一个分区,少数特殊情况才使用)
        dataStream.global().print("global");

        env.execute();


    }
}
