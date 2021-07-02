package com.atguigu.apitest.sink;

import com.alibaba.fastjson.JSON;
import com.atguigu.apitest.beans.SensorReading;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.redis.RedisSink;
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisClusterConfig;
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisPoolConfig;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommand;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommandDescription;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisMapper;

import javax.xml.crypto.Data;
import java.util.ArrayList;

/**
 * Created by John.Ma on 2021/7/1 0001 23:47
 */
public class SinkTest2_Redis {
    public static void main(String[] args) throws Exception {
        ArrayList<SensorReading> waterSensors = new ArrayList<>();
        waterSensors.add(new SensorReading("sensor_1", 1607527992000L, 20.0));
        waterSensors.add(new SensorReading("sensor_1", 1607527994000L, 50.0));
        waterSensors.add(new SensorReading("sensor_1", 1607527996000L, 50.7));
        waterSensors.add(new SensorReading("sensor_2", 1607527993000L, 10.5));
        waterSensors.add(new SensorReading("sensor_2", 1607527995000L, 30.3));

        // 连接到Redis的配置
        FlinkJedisPoolConfig  redisConfig = new FlinkJedisPoolConfig.Builder()
                .setHost("")
                .setPort(6379)
                .setMaxTotal(100)
                .setTimeout(1000 * 10)
                .build();

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.fromCollection(waterSensors)
                .addSink(new RedisSink<>(redisConfig, new RedisMapper<SensorReading>() {
                    @Override
                    public RedisCommandDescription getCommandDescription() {
                        // 返回存在Redis中的数据类型

                        return new RedisCommandDescription(RedisCommand.HSET,"sensor");
                    }

                    @Override
                    public String getKeyFromData(SensorReading sensorReading) {
                        // 从数据中获取key:Hash的key
                        return sensorReading.getId();

                    }

                    @Override
                    public String getValueFromData(SensorReading data) {
                        // 从数据中获取value,Hash的value
                        return JSON.toJSONString(data);
                    }
                }));



        env.execute();

    }
}
