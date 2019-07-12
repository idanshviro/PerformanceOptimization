package actor_map;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static akka.pattern.Patterns.gracefulStop;

public class ActorMap<K,V> extends AbstractActor {

    public static class PutMsg<K,V>{
        public final K key;
        public final V value;
        public final TimeUnit unit;
        public final long duration;

        public PutMsg(K key, V value, long duration, TimeUnit unit){
            this.key = key;
            this.value=value;
            this.unit = unit;
            this.duration = duration;
        }
    }
    public static class GetMsg<K>{
        public final K key;
        public GetMsg(K key){
            this.key = key;
        }
    }
    public static class RemoveMsg<K>{
        public final K key;
        public RemoveMsg(K key){
            this.key = key;
        }
    }
    public static class InnerRemoveMsg<K>{
        public final K key;
        public final long stamp;
        public InnerRemoveMsg(K key, long stamp){
            this.key = key;
            this.stamp = stamp;
        }
    }

    public static class SizeMsg{}

    public static class TerminateMsg {}

    /* end of messages */

    private final Map<K,StampedItem<V>> innerMap = new HashMap<>();
    private final ScheduledExecutorService cleaner = Executors.newSingleThreadScheduledExecutor();
    private final AtomicLong stateMarker = new AtomicLong(1);

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(PutMsg.class, this::put)
                .match(RemoveMsg.class, this::remove)
                .match(GetMsg.class, this::get)
                .match(SizeMsg.class, this::size)
                .match(InnerRemoveMsg.class, this::innerRemove)
                .match(TerminateMsg.class,this::terminate)
                .build();
    }
    private void terminate(TerminateMsg msg){
        cleaner.shutdownNow();
        getContext().stop(getSelf());
    }
    private void put(PutMsg msg) {
        long stamp = stateMarker.getAndIncrement();
        innerMap.put((K)msg.key, new StampedItem<>((V)msg.value,stamp));
        cleaner.schedule(()->{
            getSelf().tell(new InnerRemoveMsg<>(msg.key, stamp),
                    ActorRef.noSender());
        },msg.duration,msg.unit);
    }

    private void remove(RemoveMsg msg){
        StampedItem i = innerMap.remove(msg.key);
        if (i == null)
            sender().tell("", self());
        else
            sender().tell(i.item, self());
    }
    private void get(GetMsg msg){
        StampedItem i = innerMap.get(msg.key);
        if (i==null)
            sender().tell("", self());
        else
            sender().tell(i.item, self());
    }
    private void size(SizeMsg msg){
        sender().tell((long)innerMap.size(), self());
    }
    private void innerRemove(InnerRemoveMsg msg){
        StampedItem item = innerMap.get(msg.key);
        if (item.stamp == msg.stamp)
            innerMap.remove(msg.key);

    }
    public static void main(String[] args) throws InterruptedException {

        ActorSystem system = ActorSystem.create("PutaSystem");
        Props p = Props.create(ActorMap.class,() -> new ActorMap<Integer,String>());
        ActorRef actorMap = system.actorOf(p, "amzing");
        actorMap.tell(new PutMsg<Integer,String>(
                        1,
                        "ida",
                        3,
                        TimeUnit.SECONDS),
                actorMap);
        Thread.sleep(5000);
        system.terminate();

    }
}