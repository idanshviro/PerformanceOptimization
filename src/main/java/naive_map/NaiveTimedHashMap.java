package naive_map;

import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class NaiveTimedHashMap {
	public static class TimedHashMap<K, V> {

		HashMap<K, V> hashMap=new HashMap<>();
		HashMap<K, ScheduledFuture<?>> removalList = new HashMap<>();
		ScheduledExecutorService scheduled=Executors.newSingleThreadScheduledExecutor();
		Object lock = new Object();
		
		public int size() {
			return hashMap.size();
		}
		public V get(K key) {
			return hashMap.get(key);
		}

		public void put(K key, V value, long duration, TimeUnit timeUnit) {
			Runnable command;
			synchronized (lock) {
				hashMap.put(key, value);
			}
			
			synchronized (lock) {
				 command = (()->{
					hashMap.remove(key);	
					});
			}

			ScheduledFuture<?> currentRemoval = scheduled.schedule(command, duration, timeUnit);
			
			if(removalList.containsKey(key)) {
				removalList.get(key).cancel(true);
			}
			removalList.put(key, currentRemoval);
		}
	}
	
    private static final NaiveTimedHashMap.TimedHashMap<String, Integer> map = new NaiveTimedHashMap.TimedHashMap<>();
    public static void main(String... args) throws InterruptedException {


        map.put("time", 5, 1, TimeUnit.SECONDS);

        map.put("test", 100, 2, TimeUnit.SECONDS);
        Integer testGet = map.get("test");
        if (testGet == null || testGet != 100) {
            System.out.println("basic put +get doesnt work");
        }
        map.put("test", 99, 2, TimeUnit.SECONDS);
        testGet = map.get("test");
        if (testGet == null || testGet != 99) {
            System.out.println("put+put doesnt work");
        }
        map.put("test", 100, 3, TimeUnit.SECONDS);

        Thread.sleep(1500);
        if (map.get("time") != null) {
            System.out.println("basic delete doesnt work");
        }

        Thread.sleep(1000);
        boolean timedOverrideWorks = true;
        Integer test = map.get("test");
        if (test == null || test != 100) timedOverrideWorks = false;
        Thread.sleep(1000);
        if (map.get("test") != null) timedOverrideWorks = false;
        if (!timedOverrideWorks) {
            System.out.println("timed removal doesnt act properly");
        }

        map.put("abc", 2, 3, TimeUnit.SECONDS);
        Thread.sleep(1000);
        Integer abc = map.get("abc");
        if (abc == null || abc != 2) {
            System.out.println("value should stay after wait shorter than remove");
        }

        map.put("abc", 3, 1, TimeUnit.SECONDS);
        Thread.sleep(1500);
        if (map.get("abc") != null) {
            System.out.println("double put with less time doesnt work");
        }

        map.put("concurrent", 1, 1, TimeUnit.SECONDS);
        Thread.sleep(900);
        boolean raceWorked = true;
        for (int i = 0; i < 1000000; i++) {
            int lastValue = 2 + randomInt();
            map.put("concurrent", lastValue, 5, TimeUnit.SECONDS);
            Integer concurrent = map.get("concurrent");
            if (concurrent == null || concurrent != lastValue) {
                raceWorked = false;
                break;
            }
        }

        if (!raceWorked) {
            System.out.println("possibly concurrent puts didnt work");
        }

        System.out.println("works as expected");
        System.exit(0);
    }
	
    private static int randomInt() {
        return ThreadLocalRandom.current().nextInt();
    }
	
	
	
	
}
