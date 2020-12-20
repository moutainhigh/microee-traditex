package com.microee.traditex.inbox.app.components;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Instant;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.microee.stacks.redis.support.RedisHash;
import com.microee.traditex.inbox.oem.connector.ITridexTradFactory;
import com.microee.traditex.inbox.oem.constrants.InBoxPrefixs;
import com.microee.traditex.inbox.up.TradiTexAssists;

@Component
public class TradiTexRedis {

    public static final Integer RECONNECT_TIMEOUT = 15;

    @Value("${server.port}")
    private Integer serverPort;
    
    @Autowired
    private RedisHash redisHash;
    
    private JSONObject server = new JSONObject();
    
    @PostConstruct
    public void init() throws UnknownHostException {
        server.put("address", String.format("%s:%s", InetAddress.getLocalHost().getHostAddress(), serverPort));
        server.put("hostname", TradiTexAssists.getHostName());
    }
    
    public void writeConnection(String connid, ITridexTradFactory factory) {
        String key = InBoxPrefixs.CONNECT_STORE_PREFIX;
        JSONObject existsJsonObject = redisHash.hHasKey(key, connid) ? new JSONObject(redisHash.hget(key, connid).toString()) : null;
        JSONObject currentServer = existsJsonObject != null && existsJsonObject.has("server") ? existsJsonObject.getJSONObject("server") : server;
        Long createdAt = existsJsonObject != null && existsJsonObject.has("createdAt") ? existsJsonObject.getLong("createdAt") : Instant.now().toEpochMilli();
        JSONObject newObject = factory != null ? factory.config() : new JSONObject();
        newObject.put("server", currentServer);
        newObject.put("createdAt", createdAt);
        redisHash.hset(key, connid, newObject.toString());
    }

    public Map<?, Object> readConnection() {
        return redisHash.hmget(InBoxPrefixs.CONNECT_STORE_PREFIX);
    }

    public void removeConnection(String connid) {
        redisHash.hdel(InBoxPrefixs.CONNECT_STORE_PREFIX, connid);
    }
    
}
