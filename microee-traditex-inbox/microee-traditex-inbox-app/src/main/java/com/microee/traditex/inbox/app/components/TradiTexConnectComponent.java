package com.microee.traditex.inbox.app.components;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.PostConstruct;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.microee.traditex.inbox.oem.connector.ITridexTradFactory;
import com.microee.traditex.inbox.oem.connector.TradiTexConnection;
import com.microee.traditex.inbox.oem.constrants.ConnectStatus;
import com.microee.traditex.inbox.up.InBoxMessage;

@Component
public class TradiTexConnectComponent {

    private ConcurrentHashMap<String, TradiTexConnection<?>> connections;

    @Value("${connection.expire:300000}")
    private Long connectionExpire;
    
    @Value("${connection.ping-enable:false}")
    private Boolean pingEnable;
    
    @Autowired
    private TradiTexRedis tradiTexRedis;

    @Autowired
    private CombineMessage combineMessage;
    
    @PostConstruct
    public void init() {
        connections = new ConcurrentHashMap<>();
    }

    public void add(String connid) {
        connections.put(connid, TradiTexConnection.create(ConnectStatus.UNKNOW));
        tradiTexRedis.writeConnection(connid, null);
    }

    public ITridexTradFactory add(String connid, ITridexTradFactory val) {
        connections.put(connid, new TradiTexConnection<>(val));
        tradiTexRedis.writeConnection(connid, val);
        return val;
    }

    public void putEvent(String connid, String event, Long timestamp) {
        if (!connections.containsKey(connid)) {
            return;
        }
        ITridexTradFactory factory = this.get(connid).getFactory().putLastEvent(event).putLastTime(timestamp);
        tradiTexRedis.writeConnection(connid, factory);
    }

    public TradiTexConnection<?> get(String connid) {
        return this.connections.get(connid);
    }

    // 根据连接id获取一个连接
    @SuppressWarnings("unchecked")
    public <T> T get(String connid, Class<? extends ITridexTradFactory> T) {
        return (T) this.connections.get(connid).getFactory();
    }

    // 查看连接id是否存在
    public Boolean hasKey(String connid) {
        return this.connections.containsKey(connid);
    }
    
    // 查看一个连接是否存在
    public boolean exists(String connid) {
        return this.hasKey(connid) && this.get(connid).getFactory() != null;
    }
    
    // 根据连接id得到factory
    public ITridexTradFactory factory(String connid) {
        if (!this.exists(connid)) {
            return null;
        }
        return this.get(connid).getFactory();
    }

    // 移除一个连接
    public void remove(String connid) {
        ITridexTradFactory factory = this.factory(connid);
        if (factory == null) {
            this.connections.remove(connid);
            this.tradiTexRedis.removeConnection(connid);
            return;
        }
        factory.shutdown();
        factory = null; // 释放所有资源
        // 广播连接关闭事件, 客户端在收到这个事件时会自动发起重连
        JSONObject _times = new JSONObject();
        _times.put("time0", Instant.now().toEpochMilli()); // 收到时间
        JSONObject message = InBoxMessage.getMessage(connid, null, InBoxMessage.SHUTDOWN, connections().get(connid), _times);
        this.combineMessage.onShutDownEvent(connid, message, Instant.now().toEpochMilli());
        this.connections.remove(connid);
        this.tradiTexRedis.removeConnection(connid);
        
    }
    
    public Set<String> shutdown(String[] connids) {
        Map<String, JSONObject> conns = this.connections();
        for (Entry<String, JSONObject> entry : conns.entrySet()) {
            String connid = entry.getKey();
            if (!(connids == null || connids.length == 0)) {
                if (!Arrays.asList(connids).contains(connid)) {
                    continue;
                }
            }
            this.remove(connid);
        }
        return conns.keySet();
    }
    
    // 查看所有连接
    public Map<String, JSONObject> connections() {
        Map<?, Object> map = tradiTexRedis.readConnection();
        Map<String, JSONObject> result = new HashMap<>();
        for (Entry<?, Object> entry : map.entrySet()) {
            result.put(entry.getKey().toString(), new JSONObject(entry.getValue().toString()));
        }
        return result;
    }

    public long connectionExpire() {
        return this.connectionExpire;
    }
    
    public boolean pingEnable() {
        return this.pingEnable;
    }
    
}


