package BasicMap;

import java.util.concurrent.TimeUnit;

public class TimedValue<V> {

    private final V value;
    private final long time;

    public TimedValue(V val, long duration, TimeUnit unit){

        this.value = val;
        long mills_timestamp = unit.toMillis(duration);
        this.time = System.currentTimeMillis() + mills_timestamp;
    }

    public long getTime() {
        return time;
    }

    public V getValue() {
        return value;
    }
}
