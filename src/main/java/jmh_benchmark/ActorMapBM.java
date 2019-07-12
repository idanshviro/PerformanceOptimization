package jmh_benchmark;

import actor_map.ActorTimedHaspMap;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.RunnerException;

import BasicMap.MapInterface;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class ActorMapBM {
    final static int ITER_AMOUNT = 10000;

    @State(Scope.Thread)
    public static class MyState {
        public MapInterface.TimedSizableMap<Integer, String> map = new ActorTimedHaspMap<>();
        @Setup(Level.Trial)
        public void doSetup() {
            map = new ActorTimedHaspMap<>();
            for(int i=0;i<ITER_AMOUNT;++i){
                map.put(i, i + "", 10, TimeUnit.SECONDS);
            }
        }
        @TearDown(Level.Trial)
        public void doTearDown() {
            map.terminate();
        }
    }

    @Benchmark
    @BenchmarkMode({Mode.Throughput,Mode.SampleTime})
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Fork(value = 1)
    @Threads(value =1)
    @Warmup(iterations = 2)
    @Measurement(iterations = 4)
    public void putSingleValue(ActorMapBM.MyState state) {
        state.map.put(10_000,10_000+"",1,TimeUnit.SECONDS);
    }

    @Benchmark
    @BenchmarkMode({Mode.Throughput,Mode.SampleTime})
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Fork(value = 1)
    @Threads(value =1)
    @Warmup(iterations = 2)
    @Measurement(iterations = 4)
    public void getSingleValue(ActorMapBM.MyState state) {
        state.map.get(10_000);
    }

    @Benchmark
    @BenchmarkMode({Mode.Throughput,Mode.SampleTime})
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Fork(value = 1)
    @Threads(value =1)
    @Warmup(iterations = 2)
    @Measurement(iterations = 4)
    public void getSingleSize(ActorMapBM.MyState state) {
        state.map.size();
    }

    @Benchmark
    @BenchmarkMode({Mode.Throughput,Mode.SampleTime})
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Fork(value = 1)
    @Threads(value =1)
    @Warmup(iterations = 2)
    @Measurement(iterations = 4)
    public void putMultiValues(ActorMapBM.MyState state) {
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
    public void getMultiValues(ActorMapBM.MyState state) {
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
    public void getMultiSize(ActorMapBM.MyState state) {
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
    public void putMultiValuesMultiThreads(ActorMapBM.MyState state) throws InterruptedException {

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
    public void getMultiValuesMultiThreads(ActorMapBM.MyState state)throws InterruptedException {
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
