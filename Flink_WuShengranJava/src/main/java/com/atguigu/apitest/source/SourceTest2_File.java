package com.atguigu.apitest.source;

import com.atguigu.apitest.beans.SensorReading;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * Created by John.Ma on 2021/6/29 0029 21:44
 */
public class SourceTest2_File {
    public static void main(String[] args) throws  Exception{
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        // 从文件读取数据
        env.readTextFile("D:\\fileImportant\\Learn_projects\\learn\\Flink_WuShengranJava\\src\\main\\resources\\sensor.txt")
                .map(new RichMapFunction<String, SensorReading>() {
                    @Override
                    public SensorReading map(String value) throws Exception {
                        String[] arr = value.split(",");
                        return new SensorReading(arr[0],Long.parseLong(arr[1]),Double.parseDouble(arr[2]));
                    }
                }).print();


        env.execute("File Source Job");

    }
}
