package com.microee.traditex.inbox.app.components;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.Map.Entry;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.microee.traditex.inbox.oem.connector.ITridexTradFactory;

@Component
public class PingComponent {

    private static final Logger LOGGER = LoggerFactory.getLogger(PingComponent.class);


    @Autowired
    private TradiTexConnectComponent tradiTexConnect;

    // 检查远程原链接是否发送ping
    @Async
    @Scheduled(cron = "0/15 * * * * ?")
    public void detected() throws IOException {
        if (!tradiTexConnect.pingEnable()) {
            return;
        }
        Map<String, JSONObject> connections = tradiTexConnect.connections();
        for (Entry<String, JSONObject> entry : connections.entrySet()) {
            String connid = entry.getKey();
            JSONObject factoryConfig = entry.getValue();
            ITridexTradFactory factory = tradiTexConnect.factory(connid);
            Long createdAt = factoryConfig.getLong("createdAt"); // 连接id创建的时间
            Long now = Instant.now().toEpochMilli(); // 当前时间
            Long expire = tradiTexConnect.connectionExpire(); // 过期时间
            Long expired = factory != null && factory.lease() != null ? factory.lease() - now : (createdAt + expire) - now;
            if (expired < 0) {
                // 关闭连接
                tradiTexConnect.remove(connid);
            }
            LOGGER.info("ping-detected: connid={}, expire={}/{}, factoryNull={}, createdAt={}",
                    connid, expire, expired, factory == null, createdAt);
        }
    }

}
