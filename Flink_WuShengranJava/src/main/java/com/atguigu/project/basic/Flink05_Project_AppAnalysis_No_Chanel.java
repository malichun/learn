package com.atguigu.project.basic;

import com.atguigu.project.bean.MarketingUserBehavior;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * 6.2.2APP市场推广统计 - 不分渠道
 */
public class Flink05_Project_AppAnalysis_No_Chanel {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.addSource(new Flink04_Project_AppAnalysis_By_Chanel.AppMarketingDataSource())
            .map(new MapFunction<MarketingUserBehavior, Tuple2<Tuple2<String,String>,Long>>() {
                @Override
                public Tuple2<Tuple2<String, String>, Long> map(MarketingUserBehavior behavior) throws Exception {
                    return Tuple2.of(Tuple2.of(behavior.getChannel(), behavior.getBehavior()), 1L); //// ( (渠道, 行为) ,1)
                }
            })
            .keyBy(new KeySelector<Tuple2<Tuple2<String, String>, Long>, String>() {
                @Override
                public String getKey(Tuple2<Tuple2<String, String>, Long> value) throws Exception {
                    return value.f0.f1;
                } // T, K
            })
            .sum(1)
            .print();


        env.execute();

    }
}
