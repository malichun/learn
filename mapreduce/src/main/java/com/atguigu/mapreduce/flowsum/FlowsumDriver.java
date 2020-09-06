package com.atguigu.mapreduce.flowsum;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

public class FlowsumDriver {
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {

        //输入输出路径需要根据自己电脑的实际输入输出位置

        //1.获取配置,获取job对象实例
        Configuration configuration = new Configuration();
        Job job = Job.getInstance(configuration);

        //2.指定业务job使用的mapper/Reducer业务类
        job.setMapperClass(FlowCountMapper.class);
        job.setReducerClass(FlowCountReducer.class);

        //3.执行mapper输入数据类型
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(FlowBean.class);

        //5.指定job的输入原始文件所在目录
        FileInputFormat.setInputPaths(job,new Path(args[0]));
        FileOutputFormat.setOutputPath(job,new Path(args[1]));

        //6.指定本程序jar包所在的本地路径
        job.setJarByClass(FlowsumDriver.class);

        //7.将job中配置的相关参数,以及job所用的java类所在jar包提交给yarn去运行
        boolean result =job.waitForCompletion(true);
        System.exit(result? 0:1);






    }
}
