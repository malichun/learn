package com.atguigu.mapreduce.kv_inputformat;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.KeyValueLineRecordReader;
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

/**
 * 需求:
 * 统计输入文件中的每一行的第一个单词相同行数
 *
 * 使用KeyValueInputFormat输入
 *
 * （1）输入数据
 * banzhang ni hao
 * xihuan hadoop banzhang
 * banzhang ni hao
 * xihuan hadoop banzhang
 * （2）期望结果数据
 * banzhang	2
 * xihuan	2
 */
public class KVTextDriver {

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {

        Configuration conf = new Configuration();
/////////////
        //设置切割符!!!
        conf.set(KeyValueLineRecordReader.KEY_VALUE_SEPERATOR," ");

        //1.获取job对象
        Job job = Job.getInstance(conf);

        //2.设置jar包位置,关联mapper和reducer
        job.setJarByClass(KVTextDriver.class);
        job.setMapperClass(KVTextMapper.class);
        job.setReducerClass(KVTextReducer.class);

        //3.设置map输入kv类型
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(LongWritable.class);

        //4.设置最终输入kv类型
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(LongWritable.class);

        //5.设置输入输出的数据路径
        FileInputFormat.setInputPaths(job,new Path(args[0]));
///////////
        //设置输入格式!!!!!!!
        job.setInputFormatClass(KeyValueTextInputFormat.class);

        //6.设置输出数据路径
        FileOutputFormat.setOutputPath(job,new Path(args[1]));

        //7.提交job
        job.waitForCompletion(true);



    }
}
