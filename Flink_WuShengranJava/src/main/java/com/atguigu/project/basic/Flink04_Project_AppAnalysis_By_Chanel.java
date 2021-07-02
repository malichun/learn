package com.atguigu.project.basic;

import com.atguigu.project.bean.MarketingUserBehavior;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * 6.2.1APP市场推广统计 - 分渠道
 */
public class Flink04_Project_AppAnalysis_By_Chanel {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.addSource(new AppMarketingDataSource())
            .map(new MapFunction<MarketingUserBehavior, Tuple2<Tuple2<String,String>,Long>>() {
                @Override
                public Tuple2<Tuple2<String, String>, Long> map(MarketingUserBehavior behavior) throws Exception {
                    return Tuple2.of(Tuple2.of(behavior.getChannel(), behavior.getBehavior()), 1L); //// ( (渠道, 行为) ,1)
                }
            })
            .keyBy(new KeySelector<Tuple2<Tuple2<String, String>, Long>, Tuple2<String,String>>() {
                @Override
                public Tuple2<String, String> getKey(Tuple2<Tuple2<String, String>, Long> value) throws Exception {
                    return value.f0;
                } // T, K
            })
            .sum(1)
            .print();


        env.execute();

    }

    public static class AppMarketingDataSource extends RichSourceFunction<MarketingUserBehavior> {
        boolean canRun = true;
        Random random = new Random();
        List<String> channels = Arrays.asList("huawwei", "xiaomi", "apple", "baidu", "qq", "oppo", "vivo");
        List<String> behaviors = Arrays.asList("download", "install", "update", "uninstall");


        @Override
        public void run(SourceContext<MarketingUserBehavior> ctx) throws Exception {
            while (canRun) {
                MarketingUserBehavior marketingUserBehavior = new MarketingUserBehavior((long) random.nextInt(1000000),
                    behaviors.get(random.nextInt(behaviors.size())),
                    channels.get(random.nextInt(channels.size())),
                    System.currentTimeMillis());
                ctx.collect(marketingUserBehavior);
                Thread.sleep(20);
            }
        }

        @Override
        public void cancel() {
            canRun = false;
        }
    }
}
