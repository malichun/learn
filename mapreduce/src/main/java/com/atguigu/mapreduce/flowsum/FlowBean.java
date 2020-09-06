package com.atguigu.mapreduce.flowsum;

import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * 编写流量统计的Bean对象,自定义对象
 * 1. 实现writable接口
 */
public class FlowBean implements Writable {
    private long upFlow;
    private long donwFlow;
    private long sumFlow;

    //2.反序列化时,需要反射调用空参构造函数,所以必须有
    public FlowBean(){
        super();
    }

    /**
     * 带参构造方法
     * @param upFlow
     * @param donwFlow
     */
    public FlowBean(long upFlow, long donwFlow) {
        super();
        this.upFlow = upFlow;
        this.donwFlow = donwFlow;
        this.sumFlow = upFlow + donwFlow;
    }

    //3.写序列化方法
    @Override
    public void write(DataOutput out) throws IOException {
        out.writeLong(upFlow);
        out.writeLong(donwFlow);
        out.writeLong(sumFlow);
    }

    //4.反序列化方法
    //5.反序列化方法 读顺序 和 写序列化的写顺序必须一致
    @Override
    public void readFields(DataInput in) throws IOException {
        this.upFlow=in.readLong();
        this.donwFlow = in.readLong();
        this.sumFlow=in.readLong();
    }

    @Override
    public String toString() {
        return upFlow + "\t" + donwFlow + "\t" + sumFlow;  //输出
    }

    public long getUpFlow() {
        return upFlow;
    }

    public void setUpFlow(long upFlow) {
        this.upFlow = upFlow;
    }

    public long getDonwFlow() {
        return donwFlow;
    }

    public void setDonwFlow(long donwFlow) {
        this.donwFlow = donwFlow;
    }

    public long getSumFlow() {
        return sumFlow;
    }

    public void setSumFlow(long sumFlow) {
        this.sumFlow = sumFlow;
    }

    public void set(long upFlow2, long downFlow2) {

        upFlow = upFlow2;
        donwFlow = downFlow2;
        sumFlow = upFlow2 + downFlow2;

    }
}
