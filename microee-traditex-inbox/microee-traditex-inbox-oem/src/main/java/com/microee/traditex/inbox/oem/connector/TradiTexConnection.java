package com.microee.traditex.inbox.oem.connector;

import java.io.Serializable;
import com.microee.traditex.inbox.oem.constrants.ConnectStatus;

public class TradiTexConnection<T extends ITridexTradFactory> implements Serializable {

    private static final long serialVersionUID = 6389232545799918529L;
    
    private String status; // see ConnectorStatus
    private String lastEvent; // 最后一次接收到服务器消息的时间
    private Long lastTime; // 最后一次接收到服务器消息的时间
    private Integer healthCount; // 健康检查次数
    private T factory;
    
    public TradiTexConnection() {
        
    }

    public static TradiTexConnection<?> create(ConnectStatus status) {
        return new TradiTexConnection<>(null, status.code);
    }
    
    public TradiTexConnection(T factory) {
        this.factory = factory;
    }
    
    public TradiTexConnection(T factory, String status) {
        this.status = status;
        this.factory = factory;
    }

    public String getStatus() {
        if (this.getFactory() == null || this.getFactory().status() == null) {
            return status;
        }
        return this.getFactory().status().name();
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLastEvent() {
        if (lastEvent == null) {
            return "N/a";
        }
        return String.format("%s/%s", lastEvent, lastTime);
    }

    public TradiTexConnection<T> setLastEvent(String lastEvent) {
        this.lastEvent = lastEvent;
        return this;
    }

    public Long getLastTime() {
        return lastTime;
    }

    public TradiTexConnection<T> setLastTime(Long lastTime) {
        this.lastTime = lastTime;
        return this;
    }

    public Integer getHealthCount() {
        return healthCount;
    }

    public TradiTexConnection<T> setHealthCount(Integer healthCount) {
        this.healthCount = healthCount;
        return this;
    }

    public T getFactory() {
        return factory;
    }

    public TradiTexConnection<T> setFactory(T factory) {
        this.factory = factory;
        return this;
    }

}

