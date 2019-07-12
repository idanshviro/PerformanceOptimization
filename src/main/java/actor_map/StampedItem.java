package actor_map;

public class StampedItem<V> {
    public final V item;
    public final long stamp;

    public StampedItem(V item, long stamp){
        this.item = item;
        this.stamp = stamp;
    }

}
