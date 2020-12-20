package com.microee.traditex.inbox.app.components;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.microee.plugin.thread.ThreadPoolFactoryLow;
import com.microee.traditex.inbox.oem.connector.ITridexTradFactory;
import com.microee.traditex.inbox.oem.connector.TradiTexConnection;

// 断线重连
@Component
public class HBiTexRetryer {

    public static final int RECONNECT_TIME_SEC = 5; // 断线重连时间
    private static final Logger logger = LoggerFactory.getLogger(HBiTexRetryer.class);
    private final DelayQueue<DelayedItem<TradiTexConnection<?>>> reconnectDealyQueue = new DelayQueue<>();

    @PostConstruct
    public void init() {
        this.reconnectLoop();
    }

    // 将连接对象放入延迟队列等待重连
    public void add(TradiTexConnection<?> connection) {
        reconnectDealyQueue
                .put(new DelayedItem<>(connection, RECONNECT_TIME_SEC, TimeUnit.SECONDS));
    }
    
    public void reconnectLoop() {
        ThreadPoolFactoryLow.newInstance("traditex-inbox-断线重连线程池").pool().submit(() -> {
            try {
                while (true) {
                    DelayedItem<TradiTexConnection<?>> reconnect = reconnectDealyQueue.take();
                    ITridexTradFactory factory = reconnect.item.getFactory();
                    factory.connect(true);
                }
            } catch (InterruptedException e) {
                logger.info("系统重连延迟队列执行异常: delayQueueSize={}, erroeMessage={}", reconnectDealyQueue.size(), e.getMessage(), e);
            }
        });
    }
}

class DelayedItem<T> implements Delayed {

    private long time; /* 触发时间 */
    public T item;

    public DelayedItem(T item, long time, TimeUnit unit) {
        this.item = item;
        this.time = System.currentTimeMillis() + (time > 0 ? unit.toMillis(time) : 0);
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return time - System.currentTimeMillis();
    }

    @Override
    public int compareTo(Delayed o) {
        @SuppressWarnings("rawtypes")
        DelayedItem<?> item = (DelayedItem) o;
        long diff = this.time - item.time;
        if (diff <= 0) {// 改成>=会造成问题
            return -1;
        }
        return 1;
    }

    @Override
    public String toString() {
        return "Item{" + "time=" + time + ", name='" + item + '\'' + '}';
    }
}