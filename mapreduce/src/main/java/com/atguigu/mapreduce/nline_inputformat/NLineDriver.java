package com.atguigu.mapreduce.nline_inputformat;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.NLineInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

/**
 * 对每个单词进行个数统计，要求根据每个输入文件的行数来规定输出多少个切片。此案例要求每三行放入一个切片中。
 *
 * （1）输入数据
 *     banzhang ni hao
 *     xihuan hadoop banzhang
 *     banzhang ni hao
 *     xihuan hadoop banzhang
 *     banzhang ni hao
 *     xihuan hadoop banzhang
 *     banzhang ni hao
 *     xihuan hadoop banzhang
 *     banzhang ni hao
 *     xihuan hadoop banzhang banzhang ni hao
 *     xihuan hadoop banzhang
 * （2）期望输出数据
 * Number of splits:4
 *
 */
public class NLineDriver {
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        //1.获取job对象
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf);

/////////////
        //7设置每个切片InputSplit中划分三条记录
        NLineInputFormat.setNumLinesPerSplit(job,3);

        //8使用NLineInputFormat处理记录数
        job.setInputFormatClass(NLineInputFormat.class);
/////////////


        // 2设置jar包位置，关联mapper和reducer
        job.setJarByClass(NLineDriver.class);
        job.setMapperClass(NLineMapper.class);
        job.setReducerClass(NLineReducer.class);

        // 3设置map输出kv类型
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(LongWritable.class);

        // 4设置最终输出kv类型
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(LongWritable.class);

        // 5设置输入输出数据路径
        FileInputFormat.setInputPaths(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        // 6提交job
        job.waitForCompletion(true);

    }

}
