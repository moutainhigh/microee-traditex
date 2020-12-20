package com.microee.traditex.dashboard.app.message;

import java.nio.charset.StandardCharsets;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisMessageListener implements MessageListener {
    
    @Autowired
    private Environment environment;

    @Autowired
    private SimpMessagingTemplate template;

    private String profileActive;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String _topic = this.removeENV_NAME(new String(message.getChannel())); // 广播ws消息的时候去掉环境名
        String _message = new String(message.getBody(), StandardCharsets.UTF_8); 
        template.convertAndSend("/topic/" + _topic, _message);
    }
    
    public String removeENV_NAME(String topic) {
        this.profileActive = environment.getProperty("spring.profiles.active");
        Function<String, String> func = (redisTopic) -> {
            String s1 = "_" + this.profileActive.trim() + "$";
            String s2 = "-" + this.profileActive.trim() + "$";
            return redisTopic.replaceAll(s1, "").replaceAll(s2, "");
        };
        return func.apply(topic);
    }

}
