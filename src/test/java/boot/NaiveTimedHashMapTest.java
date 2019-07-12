package boot;


import org.junit.Test;

import BasicMap.MapInterface;
import naive_map.NaiveTimedHashMap;

import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

public class NaiveTimedHashMapTest {

    @Test
    public void shouldInsertValue(){
    	NaiveTimedHashMap.TimedHashMap<String, Integer> map = new NaiveTimedHashMap.TimedHashMap<String, Integer> ();
        assertEquals(0,map.size());
        map.put("test1",5,4,TimeUnit.SECONDS);
        assertEquals(1,map.size());
    }
    
    @Test
    public void shouldReturnNull() throws InterruptedException {
    	NaiveTimedHashMap.TimedHashMap<String, Integer> map = new NaiveTimedHashMap.TimedHashMap<String, Integer> ();
        map.put("test2",5,1,TimeUnit.SECONDS);
        Thread.sleep(1100);
        assertEquals(0,map.size());
    }
    
    @Test
    public void shouldInsertParallel() throws InterruptedException {
    	NaiveTimedHashMap.TimedHashMap<Integer, Integer> map = new NaiveTimedHashMap.TimedHashMap<Integer, Integer> ();
            Thread t1 = new Thread(()->{
                for(int i=0; i<100;i++)
                    map.put(i,i,2,TimeUnit.SECONDS);
            });

            Thread t2 = new Thread(()->{
                for(int i=100; i<200;i++)
                    map.put(i,i,2,TimeUnit.SECONDS);
            });
            t1.start();
            t2.start();

            t1.join();
            t2.join();

            assertEquals(200,map.size());
            Thread.sleep(2500);
            assertEquals(0,map.size());
}

    @Test
    public void shouldReturnLastValueOfKey() throws InterruptedException {
    	NaiveTimedHashMap.TimedHashMap<String, Integer> map = new NaiveTimedHashMap.TimedHashMap<String, Integer> ();
        map.put("test4",5,10,TimeUnit.SECONDS);
        map.put("test4",6,10,TimeUnit.SECONDS);
        assertEquals(map.get("test4").intValue(), 6);
}
    
    @Test
    public void shouldDropHalfInputs() throws InterruptedException {
    	NaiveTimedHashMap.TimedHashMap<Integer, Integer> map = new NaiveTimedHashMap.TimedHashMap<Integer, Integer> ();
        int size = 10;
        Thread t1 = new Thread(()->{
            for(int i=0;i<size;++i){
                map.put(i,i,1,TimeUnit.MICROSECONDS);
            }
        });
        Thread t2 = new Thread(()->{
            for(int i=0;i<size;++i){
                map.put(i+size,i,50,TimeUnit.SECONDS);
            }
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();
        Thread.sleep(25000);
        assertEquals(size,map.size());
    }
}
