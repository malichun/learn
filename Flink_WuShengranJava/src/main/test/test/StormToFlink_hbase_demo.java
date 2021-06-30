//package test;
//
//import com.alibaba.fastjson.JSONObject;
//import operator.*;
//import org.apache.commons.lang.StringUtils;
//import org.apache.flink.api.common.functions.FilterFunction;
//import org.apache.flink.api.common.functions.MapFunction;
//import org.apache.flink.api.common.serialization.SimpleStringSchema;
//import org.apache.flink.api.java.utils.ParameterTool;
//import org.apache.flink.streaming.api.TimeCharacteristic;
//import org.apache.flink.streaming.api.datastream.DataStream;
//import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
//import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
//import org.apache.flink.streaming.api.operators.ProcessOperator;
//import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
//import org.apache.flink.streaming.api.windowing.time.Time;
//import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
//import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
//import org.apache.flink.util.OutputTag;
//import org.slf4j.LoggerFactory;
//
//import java.util.Properties;
//
///**
// * todo 备忘记录：使用分流方式，分为单场景快速流，分为多场景或者需要查询hbase的复合流
// *
// *  todo 1，接下来要做的；了解并行度；添加水印；添加checkpoint；代码正规化；
// *
// *  todo 2, 测试slot group ，通过 slotSharingGroup
// *
// *  todo 3, disableChain,startNewChain的使用
// *
// *  todo 4, name()可以取名字哦
// */
//public class StormToFlink_hbase_demo {
//
//    private static org.slf4j.Logger logger = LoggerFactory.getLogger(StormToFlink_hbase_demo.class);
//
//    public static void main(String[] args) throws Exception {
////        String fileUrl = "D:\\wxgz-local\\resources_yace\\";
//        String fileUrl = "/zywa/job/storm/resources_new/";
//
//
//        // todo 2，读取kafka数据
//        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
//        env.setParallelism(4);
//        //todo 获取kafka的配置属性
//        args = new String[]{"--input-topic", "topn_test", "--bootstrap.servers", "node2.hadoop:9092,node3.hadoop:9092",
//                "--zookeeper.connect", "node1.hadoop:2181,node2.hadoop:2181,node3.hadoop:2181", "--group.id", "cc2"};
//
//
//        ParameterTool parameterTool = ParameterTool.fromArgs(args);
//        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
//        Properties sendPros = parameterTool.getProperties();
//        Properties pros = parameterTool.getProperties();
//
//        //todo 指定输入数据为kafka topic
//        DataStream<String> kafkaDstream = env.addSource(new FlinkKafkaConsumer<String>(
//                pros.getProperty("input-topic"),
//                new SimpleStringSchema(),
//                pros).setStartFromLatest()
//
//        ).setParallelism(4);
//
//        //todo 定义一个测流输出
//        final OutputTag<JSONObject> mutiOutputTag = new OutputTag<JSONObject>("mutiStream") {};
//
//
//
//        //todo 2，过滤掉不满足格式的数据
////        DataStream<JSONObject> jsonDstream = kafkaDstream.map(new MapOperator_01(fileUrl)).disableChaining().setParallelism(4);
//        DataStream<JSONObject> jsonDstream = kafkaDstream.map(new MapOperator_01(fileUrl)).disableChaining().name("MapOperator_01").setParallelism(4);
//
//
////        SingleOutputStreamOperator<JSONObject> splitStream = jsonDstream.process(new ProcessOperator_01(mutiOutputTag)).startNewChain().setParallelism(4);
//        SingleOutputStreamOperator<JSONObject> splitStream = jsonDstream.process(new ProcessOperator_01(mutiOutputTag)).disableChaining().setParallelism(4);
//
//        //todo 3,需要查询hbase的流
//        DataStream<JSONObject> mutiStream = splitStream.getSideOutput(mutiOutputTag);
//        mutiStream.print();
//
//        //todo 4，先做单条件流 ,去匹配场景表达式
//        DataStream<JSONObject> filterDstream = splitStream.filter(new FilterOperator_01()).disableChaining().setParallelism(4);
////        DataStream<JSONObject> filterDstream = splitStream.filter(new FilterOperator_01()).slotSharingGroup("group_03").setParallelism(4);
//
//        DataStream<JSONObject> mapDstream = filterDstream.map(new MapOperator_02()).name("MapOperator_02").setParallelism(4);
//
//
//        //todo 推送下发
//        mapDstream.filter(new FilterFunction<JSONObject>() {
//            @Override
//            public boolean filter(JSONObject json) throws Exception {
//                //推送
//                if (json.containsKey("Payload")) {
//                    return true;
//                }
//                return false;
//            }
//        }).setParallelism(4)
//                .map(new MapFunction<JSONObject, String>() {
//                    @Override
//                    public String map(JSONObject s) throws Exception {
//                        return s.toJSONString();
//                    }
//                }).setParallelism(4)
//                .addSink(new FlinkKafkaProducer010<String>(
//                        "dianyou_wx_test3",
//                        new SimpleStringSchema(),
//                        sendPros)).setParallelism(4);
//
//        //todo 下发到kafka filter
//        SingleOutputStreamOperator processDstream = mapDstream.filter(new FilterFunction<JSONObject>() {
//            @Override
//            public boolean filter(JSONObject json) throws Exception {
//                //推送
//                if (!json.containsKey("Payload")) {
//                    return true;
//                }
//                return false;
//            }
//        }).setParallelism(4)
//                .keyBy(value -> value.getString("appKey"))
//
//                .window(TumblingProcessingTimeWindows.of(Time.milliseconds(500)))
//
//                .process(new ProcessOperator())
//                .setParallelism(4);
//
//
//        //todo 发送到kafka
//        processDstream.addSink(new FlinkKafkaProducer<String>(
//                "dianyou_wx_test2",
//                new SimpleStringSchema(),
//                sendPros))
//                .setParallelism(4);
//
//
//        //todo 匹配复杂情况
//
//        DataStream<JSONObject> mutiProcessDstream = mutiStream.keyBy(value -> value.getString("appKey"))
//                .window(TumblingProcessingTimeWindows.of(Time.milliseconds(500)))
//                .process(new ProcessOperator_03())
//                .setParallelism(4);
//
//
//        //todo 批量条件
//        DataStream<JSONObject> process = mutiProcessDstream.map(new MapOperator_03())
//                .setParallelism(4)
//                .filter(new FilterFunction<JSONObject>() {
//                    @Override
//                    public boolean filter(JSONObject jsonObject) throws Exception {
//                        if (jsonObject.containsKey("tiaojian")) {
//                            return true;
//                        }
//                        return false;
//                    }
//                }).setParallelism(4)
//
//                .keyBy(value -> value.getJSONObject("logJson").getString("appKey"))
//                .window(TumblingProcessingTimeWindows.of(Time.milliseconds(500)))
//                .process(new ProcessOperator_04())
//                .setParallelism(4);
//
//
//        //todo 已经匹配到场景的情况下，先发送到topic
//
//        process.map(new MapOperator_04())
//                .setParallelism(4).
//                filter(new FilterFunction<String>() {
//                    @Override
//                    public boolean filter(String value) throws Exception {
//                        if (StringUtils.isNotBlank(value)) {
//                            return true;
//                        }
//                        return false;
//                    }
//                })
//                .setParallelism(4)
//                .addSink(new FlinkKafkaProducer010<String>(
//                        "dianyou_wx_test3",
//                        new SimpleStringSchema(),
//                        sendPros)).setParallelism(4);
//
//
//        process.map(new MapOperator_05())
//                .setParallelism(4).filter(new FilterFunction<String>() {
//            @Override
//            public boolean filter(String value) throws Exception {
//                if (StringUtils.isNotBlank(value)) {
//                    return true;
//                }
//                return false;
//            }
//        }).setParallelism(4)
//
//                .addSink(new FlinkKafkaProducer010<String>(
//                        "dianyou_wx_test2",
//                        new SimpleStringSchema(),
//                        sendPros)).setParallelism(4);
//
//        env.execute("startExecute");
//    }
//
//
//}
//
