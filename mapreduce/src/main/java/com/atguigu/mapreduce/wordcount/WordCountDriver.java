package com.atguigu.mapreduce.wordcount;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.CombineTextInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

public class WordCountDriver {
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {

//        args = new String[]{"file:///C:/Users/PC/Desktop/aa.txt","file:///C:/Users/PC/Desktop/bb"};
        //1.获取配置
        Configuration configuration = new Configuration();
        Job job = Job.getInstance(configuration);

        //2.设置jar加载路径
        job.setJarByClass(WordCountDriver.class);

        //3.设置map和reduce类
        job.setMapperClass(WordcountMapper.class);
        job.setReducerClass(WordCountReducer.class);

        //4.设置map输出
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);


//////////////////
        //优化
        // 如果不设置InputFormat，它默认用的是TextInputFormat.class
        job.setInputFormatClass(CombineTextInputFormat.class);
        //虚拟存储切片最大值设置4m,如果是生产就调大,这个是测试的
        CombineTextInputFormat.setMaxInputSplitSize(job,4194304);
//////////////////

        //5.设置最终输出kv类型
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);


        //6.设置输入和输出路径
        FileInputFormat.setInputPaths(job,new Path(args[0]));
        FileOutputFormat.setOutputPath(job,new Path(args[1]));


        //7.提交
        boolean result = job.waitForCompletion(true);

        System.exit(result ? 0 : 1);

    }
}
