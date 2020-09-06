package com.atguigu.mapreduce.kv_inputformat;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class KVTextMapper extends Mapper<Text,Text,Text, LongWritable> {

    //1.设置value
    LongWritable v = new LongWritable(1);

    @Override
    protected void map(Text key, Text value, Context context) throws IOException, InterruptedException {
        //1.封装对象

        //2.写出
        context.write(key,v);

    }

}
