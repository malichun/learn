package com.atguigu.project.basic;

import com.atguigu.project.bean.UserBehavior;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

/**
 * pv实现思路2: process
 */
public class Flink02_Project_PV {
    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env
                .readTextFile("E:\\gitdir\\learn_projects\\myLearn\\Flink_WuShengranJava\\src\\main\\resources\\UserBehavior.csv")
                .map(line -> { // 对数据切割, 然后封装到POJO中
                    String[] split = line.split(",");
                    return new UserBehavior(
                            Long.valueOf(split[0]),
                            Long.valueOf(split[1]),
                            Integer.valueOf(split[2]),
                            split[3],
                            Long.valueOf(split[4]));
                })
                .filter(behavior -> "pv".equals(behavior.getBehavior())) // 过滤出pv行为
                .keyBy(UserBehavior::getBehavior) // keyBy:按照key分组
                .process(new KeyedProcessFunction<String, UserBehavior, Long>() {
                    long count = 0 ;
                    @Override
                    public void processElement(UserBehavior value, Context ctx, Collector<Long> out) throws Exception {
                        count++;
                        out.collect(count);
                    } //K, I, O
                })
                .print();

        env.execute();
    }
}
