package com.atguigu.project;

import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;

import java.time.Duration;

/**
 * 8.1.1指定时间范围内网站总浏览量（PV）的统计
 */
public class Flink01_Project_Product_PV {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        //创建WatermarkStrategy
        WatermarkStrategy<UserBehavior> wms = WatermarkStrategy
                .<UserBehavior>forBoundedOutOfOrderness(Duration.ofSeconds(5))
                .withTimestampAssigner(new SerializableTimestampAssigner<UserBehavior>() {
                    @Override
                    public long extractTimestamp(UserBehavior userBehavior, long recordTimestamp) {
                        return userBehavior.getTimestamp() * 1000L;
                    }
                });

        env
                .readTextFile("E:\\gitdir\\learn_projects\\myLearn\\Flink_WuShengranJava\\src\\main\\resources\\UserBehavior.csv")
                .map(line -> { // 对数据进行切割,然后封装到POJO中
                    String[] split = line.split(",");
                    return new UserBehavior(Long.valueOf(split[0]), Long.valueOf(split[1]), Integer.valueOf(split[2]), split[3], Long.valueOf(split[4]));
                })
                .filter(behavior -> "pv".equals(behavior.getBehavior())) // 过滤出pv行为
                .assignTimestampsAndWatermarks(wms) // 添加wartermark
                .map(behavior -> Tuple2.of("pv", 1L))
                .returns(Types.TUPLE(Types.STRING, Types.LONG)) // 使用tuple类型,方便后面求和
                .keyBy(value -> value.f0) // keyBy:按照key分组
                .window(TumblingEventTimeWindows.of(Time.minutes(60))) // 分配窗口
                .sum(1) // 求和
                .print();

        env.execute();

    }
}
