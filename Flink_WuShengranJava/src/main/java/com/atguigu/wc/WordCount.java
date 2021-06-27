package com.atguigu.wc;

import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.operators.FlatMapOperator;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;

import java.io.InputStream;

/**
 * 批处理word count
 */
public class WordCount {
    public static void main(String[] args) throws Exception {
        //1.创建执行环境
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

        // 从文件中读取器数据
        DataSource<String> inputDataSet = env.readTextFile("D:\\fileImportant\\Learn_projects\\learn\\Flink_WuShengranJava\\src\\main\\resources\\hello.txt");

        // 对数据集进行处理,按空格分词展开,转换成(word,1L)
        DataSet<Tuple2<String,Long>> resultSet = inputDataSet
                .flatMap(new RichFlatMapFunction<String, Tuple2<String, Long>>() {
                    @Override
                    public void flatMap(String s, Collector<Tuple2<String, Long>> collector) throws Exception {
                        String[] arr = s.split(" ");
                        for (String s1 : arr) {
                            collector.collect(Tuple2.of(s1, 1L));
                        }
                    }
                })
                .groupBy(0)
                .sum(1);

        resultSet.print();

    }
}
