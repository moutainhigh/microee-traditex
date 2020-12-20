package com.microee.traditex.inbox.oem.connector;

import java.util.List;
import org.json.JSONObject;
import com.microee.traditex.inbox.oem.constrants.ConnectStatus;

public interface ITridexTradFactory {

    public ConnectStatus status();
    public Object factory();
    public String connid();
    public JSONObject config();
    public void connect(boolean retryer); // 连接或重连
    public String genOTP(String connid); // 生成一个一次性口令
    public Boolean otp(String connid, String otp); // 验证一次性口令
    public void shutdown();
    public List<String> topics();
    public void addTopic(String topic);
    public boolean removeTopics(String topic);
    public ITridexTradFactory putLastTime(Long timestamp);
    public ITridexTradFactory putLastEvent(String event);
    public void putLastPing(long epochMilli, long livetime);
    public Long lease();
    
    // 连接是否正在忙碌
    public default Boolean inuse(ConnectStatus status) {
        if (status == null) {
            return false;
        }
        if (status.name().equals(ConnectStatus.CONNECTING.name())) {
            return true;
        }
        return false;
    }
    
    // 连接是否正在忙碌
    public default Boolean busy(ConnectStatus status) {
        if (status == null) {
            return false;
        }
        if (status.name().equals(ConnectStatus.ONLINE.name())) {
            return true;
        }
        return false;
    }
    
}
