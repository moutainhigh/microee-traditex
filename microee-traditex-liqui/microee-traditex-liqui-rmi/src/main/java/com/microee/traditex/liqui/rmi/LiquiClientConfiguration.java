package com.microee.traditex.liqui.rmi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import feign.RequestInterceptor;

/**
 * 配置头部参数
 */
@Configuration("liquiClientConfiguration")
public class LiquiClientConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(LiquiClientConfiguration.class);

    @Value("${micro.services.microee-traditex-liqui-app.listOfServers}")
    private String listOfServers;

    @Bean
    public RequestInterceptor bearerHeaderAuthRequestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(feign.RequestTemplate template) {
                LOGGER.info("feign request prepared: serviceName={} url={}{}",
                        "microee-traditex-liqui-app", listOfServers, template.url());
            }
        };
    }
}
