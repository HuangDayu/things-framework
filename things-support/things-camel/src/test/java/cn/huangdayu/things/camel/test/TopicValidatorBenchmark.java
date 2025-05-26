package cn.huangdayu.things.camel.test;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

// JMH 基准测试示例
@BenchmarkMode(Mode.All) // 测试模式：平均耗时
@OutputTimeUnit(TimeUnit.NANOSECONDS) // 结果单位：纳秒
@Warmup(iterations = 3, time = 1) // 预热 3 轮，每轮 1 秒
@Measurement(iterations = 50, time = 1) // 正式测量 5 轮，每轮 1 秒
@Fork(2) // 进程数
@State(Scope.Benchmark) // 测试状态作用域
public class TopicValidatorBenchmark {

    private static ThingsMqttTopicValidator validator = new ThingsMqttTopicValidator();

    @Setup
    public void init() {
        // 初始化资源（每个 @Benchmark 方法执行前调用）
        // 初始化 10,000 个订阅主题
        for (int i = 0; i < 10_000; i++) {
            validator.addTopic("benchmark/" + i + "/sensor/+");
        }
        // 添加订阅
        validator.addTopic("sensor/+/temperature");
        validator.addTopic("device/#");
        validator.addTopic("control/+/fan/+");
    }

    @Benchmark
    public void testQuery(Blackhole bh) {
        bh.consume(validator.isDuplicateSubscription("benchmark/5555/sensor/temp"));


        // 测试用例
        bh.consume(validator.isDuplicateSubscription("sensor/123/temperature")); // true ✅

        bh.consume(validator.isDuplicateSubscription("device/123/status"));      // true ✅

        bh.consume(validator.isDuplicateSubscription("control/office/fan"));     // false ✅

        // 边界测试
        validator.addTopic("edge/#");
        bh.consume(validator.isDuplicateSubscription("edge/"));                 // true ✅

        validator.addTopic("test/+/final");
        bh.consume(validator.isDuplicateSubscription("test/abc/final"));        // true ✅
    }

    @TearDown
    public void cleanup() {
        // 资源清理
    }


    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder().include(TopicValidatorBenchmark.class.getSimpleName())
                .forks(10) // 进程数
                .warmupIterations(3) // 预热迭代
                .measurementIterations(5) // 正式测量迭代
                .mode(Mode.All) // 测试模式：所有,包括吞吐量，耗时
                .build();
        new Runner(opt).run();
    }
}
