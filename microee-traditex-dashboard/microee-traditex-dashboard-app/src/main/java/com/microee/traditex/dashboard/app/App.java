package com.microee.traditex.dashboard.app;

import java.text.ParseException;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import com.microee.stacks.redis.config.RedisEnabled;
import com.microee.stacks.starter.MainApp;
import com.microee.stacks.ws.config.WebSocketEnabled;

@EnableDiscoveryClient
@EnableAutoConfiguration()
@ComponentScan(basePackages = {"com.microee"})
@EnableFeignClients(basePackages = {"com.microee.**.rmi"})
@ImportResource("classpath:applicationContext-mybatis.xml")
@SpringBootApplication
@RedisEnabled()
@WebSocketEnabled()
public class App extends MainApp {
    public static void main(String[] args) throws ParseException {
        startup(App.class, args);
    }
}
