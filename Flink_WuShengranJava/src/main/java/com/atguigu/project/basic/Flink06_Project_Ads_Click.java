package com.atguigu.project.basic;

import com.atguigu.project.bean.AdsClickLog;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * 6.3各省份页面广告点击量实时统计
 */
public class Flink06_Project_Ads_Click {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env
            .readTextFile("E:\\gitdir\\learn_projects\\myLearn\\Flink_WuShengranJava\\src\\main\\resources\\AdClickLog.csv")
            .map(line -> {
                String[] datas = line.split(",");
                return new AdsClickLog(
                    Long.valueOf(datas[0]),
                    Long.valueOf(datas[1]),
                    datas[2],
                    datas[3],
                    Long.valueOf(datas[4]));
            })
            .map( log -> Tuple2.of(log,1L))
            .returns(Types.TUPLE(Types.GENERIC(AdsClickLog.class),Types.LONG))
            .keyBy(new KeySelector<Tuple2<AdsClickLog, Long>, Tuple2<String, Long>>() {
                @Override
                public Tuple2<String, Long> getKey(Tuple2<AdsClickLog, Long> value) throws Exception {
                    return Tuple2.of(value.f0.getProvince(),value.f0.getAdId());
                }
            })
            .sum(1)
            .print("省份-广告");

        env.execute();

    }
}
