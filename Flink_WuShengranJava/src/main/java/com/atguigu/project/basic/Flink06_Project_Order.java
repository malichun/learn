package com.atguigu.project.basic;

import com.atguigu.project.bean.OrderEvent;
import com.atguigu.project.bean.TxEvent;
import org.apache.flink.streaming.api.datastream.ConnectedStreams;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.CoProcessFunction;
import org.apache.flink.util.Collector;

import java.util.HashMap;
import java.util.Map;

/**
 * 6.4订单支付实时监控
 * 在电商网站中，订单的支付作为直接与营销收入挂钩的一环，在业务流程中非常重要。
 * 对于订单而言，为了正确控制业务流程，也为了增加用户的支付意愿，网站一般会设置一个支付失效时间，超过一段时间不支付的订单就会被取消。
 * 另外，对于订单的支付，我们还应保证用户支付的正确性，这可以通过第三方支付平台的交易数据来做一个实时对账。
 *
 * 需求: 来自两条流的订单交易匹配
 *
 * 对于订单支付事件，用户支付完成其实并不算完，我们还得确认平台账户上是否到账了。
 * 而往往这会来自不同的日志信息，所以我们要同时读入两条流的数据来做合并处理。
 *
 *  * 订单数据从OrderLog.csv中读取，交易数据从ReceiptLog.csv中读取
 *
 */
public class Flink06_Project_Order {
    public static void main(String[] args) throws Exception{
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);
        // 1.读取Order流
        SingleOutputStreamOperator<OrderEvent> orderEventDS = env
            .readTextFile("E:\\gitdir\\learn_projects\\myLearn\\Flink_WuShengranJava\\src\\main\\resources\\OrderLog.csv")
            .map(line -> {
                String[] datas = line.split(",");
                return new OrderEvent(
                    Long.valueOf(datas[0]),
                    datas[1],
                    datas[2],
                    Long.valueOf(datas[3])
                );
            });


        // 2.读取交易数据
        SingleOutputStreamOperator<TxEvent> txDS = env
            .readTextFile("E:\\gitdir\\learn_projects\\myLearn\\Flink_WuShengranJava\\src\\main\\resources\\ReceiptLog.csv")
            .map(line -> {
                String[] datas = line.split(",");
                return new TxEvent(datas[0], datas[1], Long.valueOf(datas[2]));
            });

        // 3.两个流连接在一次
        ConnectedStreams<OrderEvent, TxEvent> orderAndTx = orderEventDS.connect(txDS);

        //4 因为不同的数据流到大的先后顺序不一致,所以需要匹配对账信息,输出表示对账成功与否
        orderAndTx
            .keyBy("txId","txId")
            .process(new CoProcessFunction<OrderEvent, TxEvent, String>() {
                // 存tx -> OrderEvent
                Map<String,OrderEvent> orderMap = new HashMap<>();
                // 存txId -> TxEvent
                Map<String,TxEvent> txMap = new HashMap<>();
                @Override
                public void processElement1(OrderEvent value, Context ctx, Collector<String> out) throws Exception {
                    // 获取交易信息
                    if(txMap.containsKey(value.getTxId())){
                        out.collect("订单: "+ value + " 对账成功");
                        txMap.remove(value.getTxId());
                    }else{
                        orderMap.put(value.getTxId(),value);
                    }
                }

                @Override
                public void processElement2(TxEvent value, Context ctx, Collector<String> out) throws Exception {
                    if(orderMap.containsKey(value.getTxId())){
                        OrderEvent orderEvent = orderMap.remove(value.getTxId());
                        out.collect("订单: " + orderEvent + " 对账成功");
                    }else {
                        txMap.put(value.getTxId(),value);
                    }
                }
            })
            .print();
        env.execute();

    }
}
