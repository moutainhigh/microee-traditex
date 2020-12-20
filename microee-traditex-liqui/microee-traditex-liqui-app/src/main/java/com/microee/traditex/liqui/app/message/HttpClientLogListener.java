package com.microee.traditex.liqui.app.message;

import java.nio.charset.StandardCharsets;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import com.microee.traditex.liqui.app.service.LiquiRiskService;
import okhttp3.HttpUrl;

@Component
public class HttpClientLogListener implements MessageListener {

    @Autowired
    private LiquiRiskService liquiRiskService;
    
    @Override
    public void onMessage(Message message, byte[] pattern) {
        String _message = new String(message.getBody(), StandardCharsets.UTF_8); 
        JSONObject httpLog = new JSONObject(_message);
        Long start = httpLog.getLong("start");
        Long speed = httpLog.getLong("speed");
        String URL = httpLog.getString("URL");
        JSONObject headers = httpLog.getJSONObject("headers");
        String connid = headers.has("connid") ? headers.getString("connid") : null;
        HttpUrl httpUrl = HttpUrl.parse(URL);
        String schema = httpUrl.uri().getScheme();
        String path = httpUrl.uri().getPath();
        String host = httpUrl.uri().getHost();
        liquiRiskService.alertNetwork(connid, schema, host, path, start, speed); 
    }

}
