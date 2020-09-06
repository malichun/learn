package com.atguigu.mapreduce.user_inputformat;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;

import java.io.IOException;

/**
 * 1．需求
 * 将多个小文件合并成一个SequenceFile文件
 *    （SequenceFile文件是Hadoop用来存储二进制形式的key-value对的文件格式），
 *    SequenceFile里面存储着多个文件，
 *    存储的形式为文件路径+名称为key，文件内容为value。
 *
 *
 */
public class SequenceFileDriver {
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {

        // 1 获取job对象
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf);


        // 2 设置jar包存储位置、关联自定义的mapper和reducer
        job.setJarByClass(SequenceFileDriver.class);
        job.setMapperClass(SequenceFileMapper.class);
        job.setReducerClass(SequenceFileReducer.class);
/////////
        // 7设置输入的inputFormat
        job.setInputFormatClass(WholeFileInputFormat.class);

        // 8设置输出的outputFormat
        job.setOutputFormatClass(SequenceFileOutputFormat.class);
/////////


// 3 设置map输出端的kv类型
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(BytesWritable.class);

        // 4 设置最终输出端的kv类型
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(BytesWritable.class);

        // 5 设置输入输出路径
        FileInputFormat.setInputPaths(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        // 6 提交job
        boolean result = job.waitForCompletion(true);
        System.exit(result ? 0 : 1);

    }
}
