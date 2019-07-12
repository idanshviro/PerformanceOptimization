package naive_benchmarks;

import actor_map.ActorTimedHaspMap;
import naive_map.NaiveTimedHashMap;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.Main;
import org.openjdk.jmh.runner.RunnerException;

import BasicMap.MapInterface;

public class NaiveCompareBM {
    private static final long WARMUP_ITER = (long) 10000;
    private static final long BENCHMARK_ITER = (long) 1000000;

    public static void main(String... args) throws RunnerException, IOException {
        Main.main(args);
    }

    public void actorMapBMSetSizeGet(){
        final MapInterface.TimedSizableMap<Integer, Integer> actor = new ActorTimedHaspMap<>();

        //warmup
        long start = System.nanoTime();
        for (int i = 0; i < WARMUP_ITER; i++) {
            actor.put(i,i,10,TimeUnit.MILLISECONDS);
            actor.size();
            actor.get(i);
        }
        long end = System.nanoTime();

        System.gc();
        //benchmark
        long actorStart = System.nanoTime();
        for (int i = 0; i < BENCHMARK_ITER; i++) {
            actor.put(i,i,10,TimeUnit.MILLISECONDS);
            actor.size();
            actor.get(i);
        }
        long actorEnd = System.nanoTime();
        actor.terminate();
        System.out.println("actor: " + (actorEnd - actorStart)/1E6 + "ms");
    }

    public void NaiveMapBMSetSizeGet(){
        final NaiveTimedHashMap.TimedHashMap<Integer, Integer> map = new NaiveTimedHashMap.TimedHashMap<>();

        //warmup
        long start = System.nanoTime();
        for (int i = 0; i < WARMUP_ITER; i++) {
            map.put(i,i,10,TimeUnit.MILLISECONDS);
            map.size();
            map.get(i);
        }
        long end = System.nanoTime();

        System.gc();
        //benchmark
        long naiveStart = System.nanoTime();
        for (int i = 0; i < BENCHMARK_ITER; i++) {
            map.put(i,i,10,TimeUnit.MILLISECONDS);
            map.size();
            map.get(i);
        }
        long naiveEnd = System.nanoTime();
        System.out.println("NaiveTimedHashMap: " + (naiveEnd - naiveStart)/1E6 + "ms");
    }

    public void actorMapBMGet(){
        final MapInterface.TimedSizableMap<Integer, Integer> actor = new ActorTimedHaspMap<>();

        //warmup
        for (int i = 0; i < WARMUP_ITER; i++) {
            actor.put(i,i,1,TimeUnit.SECONDS);
        }
        for (int i = 0; i < WARMUP_ITER; i++) {
            actor.get(i);
        }

        try {
            Thread.sleep(1100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.gc();
        //benchmark
        for (int i = 0; i < BENCHMARK_ITER; i++) {
            actor.put(i,i,1,TimeUnit.SECONDS);
        }
        long actorStart = System.nanoTime();
        for (int i = 0; i < BENCHMARK_ITER; i++) {
            actor.get(i);
        }
        long actorEnd = System.nanoTime();
        actor.terminate();
        System.out.println("actor: " + (actorEnd - actorStart)/1E6 + "ms");
    }

    public void naiveMapBMGet(){
        final NaiveTimedHashMap.TimedHashMap<Integer, Integer> map = new NaiveTimedHashMap.TimedHashMap<>();

        //warmup
        for (int i = 0; i < WARMUP_ITER; i++) {
            map.put(i,i,1,TimeUnit.SECONDS);
        }
        for (int i = 0; i < WARMUP_ITER; i++) {
            map.get(i);
        }

        try {
            Thread.sleep(1100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.gc();
        //benchmark
        for (int i = 0; i < BENCHMARK_ITER; i++) {
            map.put(i,i,1,TimeUnit.SECONDS);
        }
        long naiveStart = System.nanoTime();
        for (int i = 0; i < BENCHMARK_ITER; i++) {
            map.get(i);
        }
        long naiveEnd = System.nanoTime();
        System.out.println("NaiveTimedHashMap: " + (naiveEnd - naiveStart)/1E6 + "ms");
    }


}
