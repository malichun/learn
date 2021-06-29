package com.atguigu.apitest.source;

import com.atguigu.apitest.beans.SensorReading;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by John.Ma on 2021/6/29 0029 22:13
 */
public class SourceTest4_UDF {
    public static void main(String[] args) throws Exception{
        // 创建执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStream<SensorReading> dataStream = env.addSource(new MySensorSource());

        dataStream.print();

        env.execute();
    }

    //实现自定义SourceFunction
    public static class MySensorSource implements SourceFunction<SensorReading>{

        // 定义一个标识位,用来控制数据产生
        private volatile boolean running = true;


        @Override
        public void run(SourceContext<SensorReading> ctx) throws Exception {
            //定义一个随机数发生器
            Random random = new Random();
            //设置10个传感器的初始温度
            Map<String,Double> sensorTempMap = new HashMap<>();
            for(int i =0;i<10;i++){
                sensorTempMap.put("sensor_"+(i+1),60 + random.nextGaussian()* 20);
            }

            while(running){
                for(String sensorId :sensorTempMap.keySet()){
                    // 在当前温度的基础上做随机波动
                    Double newTemp= sensorTempMap.get(sensorId) + random.nextGaussian();
                    sensorTempMap.put(sensorId,newTemp);
                    ctx.collect(new SensorReading(sensorId, System.currentTimeMillis(),newTemp));
                }
                //控制输出评率
                Thread.sleep(1000L);
            }
        }

        @Override
        public void cancel() {
            running = false;
        }
    }

}


