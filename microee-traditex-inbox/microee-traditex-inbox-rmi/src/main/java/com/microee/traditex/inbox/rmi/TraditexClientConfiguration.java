package com.microee.traditex.inbox.rmi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import feign.RequestInterceptor;

/**
 * 配置头部参数
 */
@Configuration("tradiTexRMiConfiguration")
public class TraditexClientConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(TraditexClientConfiguration.class);

    @Value("${micro.services.microee-traditex-inbox-app.listOfServers}")
    private String listOfServers;

    @Bean
    public RequestInterceptor bearerHeaderAuthRequestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(feign.RequestTemplate template) {
                LOGGER.info("feign request prepared: serviceName={} url={}{}",
                        "microee-traditex-inbox-app", listOfServers, template.url());
            }
        };
    }
}
