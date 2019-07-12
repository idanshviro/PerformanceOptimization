package jmh_benchmark;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.RunnerException;

import naive_map.NaiveTimedHashMap;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class NaiveMapBM {
    final static int ITER_AMOUNT = 10000;

    @State(Scope.Thread)
    public static class MyState {
        public NaiveTimedHashMap.TimedHashMap<Integer, String> map = new NaiveTimedHashMap.TimedHashMap<>();
        @Setup(Level.Trial)
        public void doSetup() {
            for(int i=0;i<ITER_AMOUNT;++i){
                map.put(i, i + "", 10, TimeUnit.SECONDS);
            }
        }
        @TearDown(Level.Trial)
        public void doTearDown() {
        }
    }

    @Benchmark
    @BenchmarkMode({Mode.Throughput,Mode.SampleTime})
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Fork(value = 1)
    @Threads(value =1)
    @Warmup(iterations = 2)
    @Measurement(iterations = 4)
    public void putSingleValue(MyState state) {
        state.map.put(10000,10000+"",1,TimeUnit.SECONDS);
    }

    @Benchmark
    @BenchmarkMode({Mode.Throughput,Mode.SampleTime})
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Fork(value = 1)
    @Threads(value =1)
    @Warmup(iterations = 2)
    @Measurement(iterations = 4)
    public void getSingleValue(MyState state) {
        state.map.get(10000);
    }

    @Benchmark
    @BenchmarkMode({Mode.Throughput,Mode.SampleTime})
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Fork(value = 1)
    @Threads(value =1)
    @Warmup(iterations = 2)
    @Measurement(iterations = 4)
    public void getSingleSize(MyState state) {
        state.map.size();
    }

    @Benchmark
    @BenchmarkMode({Mode.Throughput,Mode.SampleTime})
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Fork(value = 1)
    @Threads(value =1)
    @Warmup(iterations = 2)
    @Measurement(iterations = 4)
    public void putMultiValues(MyState state) {
        for(int i =0; i<ITER_AMOUNT;i++)
            state.map.put(i,i+"",1,TimeUnit.SECONDS);
    }

    @Benchmark
    @BenchmarkMode({Mode.Throughput,Mode.SampleTime})
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Fork(value = 1)
    @Threads(value =1)
    @Warmup(iterations = 2)
    @Measurement(iterations = 4)
    public void getMultiValues(MyState state) {
        for(int i =0; i<ITER_AMOUNT;i++)
            Optional.ofNullable(state.map.get(i));
    }

    @Benchmark
    @BenchmarkMode({Mode.Throughput,Mode.SampleTime})
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Fork(value = 1)
    @Threads(value =1)
    @Warmup(iterations = 2)
    @Measurement(iterations = 4)
    public void getMultiSize(MyState state) {
        for(int i =0; i<ITER_AMOUNT;i++)
            state.map.size();
    }

    @Benchmark
    @BenchmarkMode({Mode.Throughput,Mode.SampleTime})
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Fork(value = 1)
    @Threads(value =1)
    @Warmup(iterations = 2)
    @Measurement(iterations = 4)
    public void putMultiValuesMultiThreads(MyState state) throws InterruptedException {

        Thread t1 = new Thread(()->{
            for(int i=0; i<100;i++)
                state.map.put(i,i+"",1,TimeUnit.SECONDS);
        });

        Thread t2 = new Thread(()->{
            for(int i=100; i<200;i++)
                state.map.put(i,i+"",1,TimeUnit.SECONDS);
        });
        t1.start();
        t2.start();

        t1.join();
        t2.join();
    }

    @Benchmark
    @BenchmarkMode({Mode.Throughput,Mode.SampleTime})
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Fork(value = 1)
    @Threads(value =1)
    @Warmup(iterations = 2)
    @Measurement(iterations = 4)
    public void getMultiValuesMultiThreads(MyState state)throws InterruptedException {
        Thread t1 = new Thread(()->{
            for(int i=0; i<100;i++)
                Optional.ofNullable(state.map.get(i));
        });

        Thread t2 = new Thread(()->{
            for(int i=100; i<200;i++)
                Optional.ofNullable(state.map.get(i));
        });
        t1.start();
        t2.start();

        t1.join();
        t2.join();
    }

    public static void main(String ...args) throws IOException, RunnerException {
        org.openjdk.jmh.Main.main(args);
    }
}
