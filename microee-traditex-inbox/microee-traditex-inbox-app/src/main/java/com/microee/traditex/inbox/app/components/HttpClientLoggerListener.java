package com.microee.traditex.inbox.app.components;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.microee.plugin.http.assets.HttpClientLogger;
import com.microee.stacks.redis.support.RedisMessage;
import okhttp3.Headers;

@Component
public class HttpClientLoggerListener implements HttpClientLogger {

    @Autowired
    private RedisMessage redisMessage;

    @Value("${topic.traditex.httplog.listener.message}")
    private String tradiTexHttpLogListererMessageTopic;

    public Map<String, String> getHeadersMap(Headers headers) {
        Set<String> set = headers.names();
        Map<String, String> map = new HashMap<>(set.size());
        for (String s : set) {
            map.put(s, headers.get(s));
        }
        return map;
    }

    @Override
    public void log(String method, String url, Headers headers, String bodyString,
            String errorMessage, Long start, Long speed, Boolean isSSl, String proxyString) { 
        Map<String, Object> map = new HashMap<>();
        map.put("start", start);
        map.put("speed", speed);
        map.put("method", method);
        map.put("URL", url);
        map.put("errorMessage", errorMessage);
        map.put("headers", getHeadersMap(headers));
        redisMessage.send(tradiTexHttpLogListererMessageTopic, map);
    }

}
