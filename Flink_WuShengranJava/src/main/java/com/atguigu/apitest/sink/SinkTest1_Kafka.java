package com.atguigu.apitest.sink;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

/**
 * Created by John.Ma on 2021/7/1 0001 23:28
 */
public class SinkTest1_Kafka {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStreamSource<String> inputStream = env.socketTextStream("www.bigdata01.com", 7777);

        inputStream.addSink(new FlinkKafkaProducer<String>("www.bigdata02.com:9092,www.bigdata03.com:9092,www.bigdata04.com:9092", "test", new SimpleStringSchema()));

        env.execute();
    }

}
