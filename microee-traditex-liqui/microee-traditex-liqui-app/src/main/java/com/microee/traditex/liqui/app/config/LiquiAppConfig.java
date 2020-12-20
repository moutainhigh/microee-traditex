package com.microee.traditex.liqui.app.config;

import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import com.microee.plugin.commons.LongGenerator;
import com.microee.traditex.inbox.rmi.TradiTexConnectorClient;
import com.microee.traditex.liqui.app.core.conn.ConnectServiceMap;
import com.microee.traditex.liqui.app.core.conn.ConnectBootstrap;
import com.microee.traditex.liqui.app.message.HttpClientLogListener;

@Configuration
public class LiquiAppConfig extends WebMvcConfigurerAdapter
        implements ApplicationListener<ApplicationEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LiquiAppConfig.class);
    
    @Autowired
    private ConnectBootstrap setupConnectService;

    @Autowired
    private TradiTexConnectorClient tradiTexConnectorClient;

    @Autowired
    private ConnectServiceMap connectServiceMap;

    @Value("${topic.traditex.httplog.listener.message}")
    private String tradiTexHttpLogListererMessageTopic;
    
    @Bean
    public LongGenerator longGenerator() {
        return new LongGenerator(0,0);
    }

    @Autowired
    private HttpClientLogListener redisMessageListener;
    
    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(@Autowired RedisConnectionFactory redisConnectionFactory) {
        RedisMessageListenerContainer redisMessageListenerContainer = new RedisMessageListenerContainer();
        redisMessageListenerContainer.setConnectionFactory(redisConnectionFactory);
        redisMessageListenerContainer.addMessageListener(redisMessageListener, new ChannelTopic(tradiTexHttpLogListererMessageTopic));
        redisMessageListenerContainer.setErrorHandler(e -> LOGGER.error("There was an error in redis message listener container", e));
        redisMessageListenerContainer.setTaskExecutor(Executors.newFixedThreadPool(4));
        return redisMessageListenerContainer;
    }
    
    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ApplicationReadyEvent) {
            LOGGER.info("TradiTex Liqui 启动成功, event={}", event.getClass().getName());
            setupConnectService.add(setupConnectService);
        }
        if (event instanceof ContextClosedEvent) {
            tradiTexConnectorClient.shutdown(connectServiceMap.connids());
            LOGGER.info("TradiTex Liqui 正常关闭, evnent={}", event.getClass().getName());
        }
    }

}
