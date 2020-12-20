package com.microee.traditex.dashboard.app.config;

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
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import com.microee.plugin.thread.NamedThreadFactory;
import com.microee.traditex.dashboard.app.message.RedisMessageListener;

@Configuration
public class TradiTexAppConfig implements SchedulingConfigurer, ApplicationListener<ApplicationEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TradiTexAppConfig.class);

    @Value("${topic.traditex.orderbook.message}")
    private String tradiTexOrderBookMessageTopic;

    @Value("${topic.traditex.pricing.message}")
    private String tradiTexPricingTopic;
    
    @Value("${topic.traditex.diskorders.message}")
    private String tradiTexDiskOrdersMessageTopic;
    
    @Value("${topic.traditex.revokeorder.count.message}")
    private String tradiTexRevokeOrderCountMessageTopic;
    
    @Value("${topic.traditex.httpnetwork.message}")
    private String tradiTexHttpNetworkMessageTopic;
    
    @Value("${topic.traditex.account.balance.message}")
    private String tradiTexAccountBalanceMessageTopic;

    @Value("${topic.traditex.account-disk.balancess.message}")
    private String tradiTexAccountDiskBalancessMessageTopic;

    @Value("${topic.traditex.account-solr.balancess.message}")
    private String tradiTexAccountSolrBalancessMessageTopic;
    
    @Value("${topic.traditex.httplog.listener.message}")
    private String tradiTexHttpLogListererMessageTopic;

    @Autowired
    private RedisMessageListener redisMessageListener;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(3);
        taskScheduler.initialize();
        taskScheduler.setThreadFactory(new NamedThreadFactory("ARCHIVE-THREAD"));
        taskRegistrar.setTaskScheduler(taskScheduler);
    }
    
    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(@Autowired RedisConnectionFactory redisConnectionFactory) {
        RedisMessageListenerContainer redisMessageListenerContainer = new RedisMessageListenerContainer();
        redisMessageListenerContainer.setConnectionFactory(redisConnectionFactory);
        redisMessageListenerContainer.addMessageListener(redisMessageListener, new ChannelTopic(tradiTexPricingTopic));
        redisMessageListenerContainer.addMessageListener(redisMessageListener, new ChannelTopic(tradiTexOrderBookMessageTopic));
        redisMessageListenerContainer.addMessageListener(redisMessageListener, new ChannelTopic(tradiTexDiskOrdersMessageTopic));
        redisMessageListenerContainer.addMessageListener(redisMessageListener, new ChannelTopic(tradiTexHttpNetworkMessageTopic));
        redisMessageListenerContainer.addMessageListener(redisMessageListener, new ChannelTopic(tradiTexRevokeOrderCountMessageTopic));
        redisMessageListenerContainer.addMessageListener(redisMessageListener, new ChannelTopic(tradiTexAccountBalanceMessageTopic));
        redisMessageListenerContainer.addMessageListener(redisMessageListener, new ChannelTopic(tradiTexAccountDiskBalancessMessageTopic));
        redisMessageListenerContainer.addMessageListener(redisMessageListener, new ChannelTopic(tradiTexAccountSolrBalancessMessageTopic));
        redisMessageListenerContainer.addMessageListener(redisMessageListener, new ChannelTopic(tradiTexHttpLogListererMessageTopic));
        redisMessageListenerContainer.setErrorHandler(e -> LOGGER.error("There was an error in redis message listener container", e));
        redisMessageListenerContainer.setTaskExecutor(Executors.newFixedThreadPool(4));
        return redisMessageListenerContainer;
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ApplicationReadyEvent) {
            LOGGER.info("TradiTex dashboard 启动成功");
        }
    }
}
