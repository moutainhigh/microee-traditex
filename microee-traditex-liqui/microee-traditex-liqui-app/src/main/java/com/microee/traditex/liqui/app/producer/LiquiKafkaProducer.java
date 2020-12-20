package com.microee.traditex.liqui.app.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.microee.stacks.kafka.support.KafkaStringProducer;

@Component
public class LiquiKafkaProducer {

    @Value("${topics.liqui.riskalter}")
    private String riskAlterMessageTopic;

    @Autowired
    private KafkaStringProducer kafkaStringProducer;

    public void sendMessage(String _message) {
        kafkaStringProducer.sendMessage(riskAlterMessageTopic, _message);
    }
    
}
