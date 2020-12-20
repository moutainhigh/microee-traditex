package com.microee.traditex.inbox.app.components;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.microee.stacks.kafka.support.KafkaStringProducer;

@Component
public class TradiTexKafkaProducer {

    @Autowired(required=false)
    private KafkaStringProducer kafkaStringProducer;
    
    @Value("${topics.inbox.orderbook}")
    private String orderBookTopic;
    
    @Value("${topics.inbox.connected}")
    private String connectedTopic;
    
    @Value("${topics.inbox.pricing}")
    private String pricingTopic;
    
    @Value("${topics.inbox.banalce}")
    private String banalceTopic;
    
    @Value("${topics.inbox.orderstat}")
    private String orderStatTopic;
    
    @Value("${topics.inbox.authevent}")
    private String authEventTopic;
    
    @Value("${topics.inbox.subscribe.event}")
    private String subscribeEventTopic;
    
    @Value("${topics.inbox.connect.shutdown.event}")
    private String connectShutDownEventTopic;


    /**
     * 广播连接建立成功事件
     * @param message
     */
    public void subscribeEventBroadcase(JSONObject message) {
    	if (kafkaStringProducer == null) {
    		return;
    	}
        kafkaStringProducer.sendMessage(subscribeEventTopic, message.toString());
    }
    
    /**
     * 广播连接建立成功事件
     * @param message
     */
    public void connectedBroadcase(JSONObject message) {
    	if (kafkaStringProducer == null) {
    		return;
    	}
        kafkaStringProducer.sendMessage(connectedTopic, message.toString());
    }

    /**
     * 广播外汇价格变动事件
     * @param message
     */
    public void pricingBroadcase(JSONObject message) {
    	if (kafkaStringProducer == null) {
    		return;
    	}
        kafkaStringProducer.sendMessage(pricingTopic, message.toString());
    }
    
    /**
     * 广播 orderbook
     * 
     * @param message
     */
    public void orderBookBroadcase(JSONObject message) {
    	if (kafkaStringProducer == null) {
    		return;
    	}
        kafkaStringProducer.sendMessage(orderBookTopic, message.toString());
    }
    
    /**
     * 广播 account balance change
     * 
     * @param message
     */
    public void balanceBroadcase(JSONObject message) {
    	if (kafkaStringProducer == null) {
    		return;
    	}
        kafkaStringProducer.sendMessage(banalceTopic, message.toString());
    }
    
    /**
     * 广播 order state change
     * 
     * @param message
     */
    public void orderStateBroadcase(JSONObject message) {
    	if (kafkaStringProducer == null) {
    		return;
    	}
        kafkaStringProducer.sendMessage(orderStatTopic, message.toString());
    }
    
    // 广播鉴权事件
    public void authEventBroadcase(JSONObject message) {
    	if (kafkaStringProducer == null) {
    		return;
    	}
        kafkaStringProducer.sendMessage(authEventTopic, message.toString());
    }

    // 广播连接关闭事件
    public void shutdownBroadcase(JSONObject message) {
    	if (kafkaStringProducer == null) {
    		return;
    	}
        kafkaStringProducer.sendMessage(connectShutDownEventTopic, message.toString());
    }
    
}
