package com.microee.traditex.liqui.app;

import java.text.ParseException;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.scheduling.annotation.EnableScheduling;
import com.microee.stacks.kafka.config.KafkaEnabled;
import com.microee.stacks.redis.config.RedisEnabled;
import com.microee.stacks.starter.MainApp;

@ComponentScan(basePackages = {"com.microee"})
@EnableFeignClients(basePackages = {"com.microee.**.rmi"})
@ImportResource("classpath:applicationContext-mybatis.xml")
@SpringBootApplication
@KafkaEnabled(enable = {})
@RedisEnabled()
@EnableScheduling
public class App extends MainApp {
    public static void main(String[] args) throws ParseException {
        startup(App.class, args);
    }
}

