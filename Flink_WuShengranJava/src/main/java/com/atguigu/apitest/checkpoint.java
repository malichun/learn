package com.atguigu.apitest;

import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.runtime.executiongraph.restart.RestartStrategy;
import org.apache.flink.runtime.state.filesystem.FsStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.TimeUtils;

import java.util.concurrent.TimeUnit;

/**
 * Created by John.Ma on 2021/7/5 0005 20:59
 */
public class checkpoint {
    public static void main(String[] args) {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // 1.状态后端配置
        env.setStateBackend(new FsStateBackend(""));

        // 2. 每 1000ms 开始一次 checkpoint
        env.enableCheckpointing(1000); // (每1000ms让jobManager进行一次checkpoint检查
        // 高级选项
        //设置模式为精确一次 (这是默认值)
        env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        // 确认 checkpoints 之间的时间会进行 500 ms
        env.getCheckpointConfig().setMinPauseBetweenCheckpoints(500);
        // Checkpoint 必须在一分钟内完成，否则就会被抛弃
        env.getCheckpointConfig().setCheckpointTimeout(60000);
        // // 同一时间只允许一个 checkpoint 进行
        env.getCheckpointConfig().setMaxConcurrentCheckpoints(1);
        // 开启在 job 中止后仍然保留的 externalized checkpoints  // 作业取消时外部化检查点的清理行为, 在作业取消时保留外部检查点。
        env.getCheckpointConfig().enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
        // 设置可容忍的失败次数（默认0，即checkpoint处理失败，就当作程序执行异常）
        env.getCheckpointConfig().setTolerableCheckpointFailureNumber(3);

        // 3.重启策略
        // 固定延迟时间 //(最多尝试3次，每次间隔10s)
        env.setRestartStrategy(
            RestartStrategies.fixedDelayRestart(
                3,
                Time.of(10, TimeUnit.SECONDS) // 每隔1
            )
        );

        // step3 存到哪儿


    }
}
