package com.microee.traditex.inbox.up.oanda;

import java.time.Instant;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.microee.traditex.inbox.oem.constrants.ConnectStatus;
import com.microee.traditex.inbox.oem.constrants.VENDER;
import com.microee.traditex.inbox.up.CombineMessageListener;
import com.microee.traditex.inbox.up.InBoxMessage;
import okhttp3.Request;

public class OandaStreamHandler {

    private static final Logger logger = LoggerFactory.getLogger(OandaStreamHandler.class);
    public static final VENDER _VENDER = VENDER.Oanda;

    private String connid;
    private ConnectStatus connectStatus;
    private final CombineMessageListener combineMessageListener;

    public OandaStreamHandler(final String connid, final CombineMessageListener combineMessageListener) {
        this.connid = connid;
        this.connectStatus = ConnectStatus.UNKNOW;
        this.combineMessageListener = combineMessageListener;
    }

    public ConnectStatus getConnectStatus() {
        return connectStatus;
    }

    public void setConnectStatus(ConnectStatus connectStatus) {
        this.connectStatus = connectStatus;
    }

    public void onStreamMessage(Request request, String line) {
        Long receiveTime = Instant.now().toEpochMilli();
        JSONObject _times = new JSONObject();
        JSONObject lineObject = new JSONObject(line);
        _times.put("timeA", receiveTime); // 收到时间
        if (lineObject.has("errorMessage")) {
            this.setConnectStatus(ConnectStatus.FAILED);
            _times.put("timeB", Instant.now().toEpochMilli()); // 处理时间
            JSONObject config = new JSONObject();
            config.put("url", request.url().toString());
            config.put("headers", request.headers());
            this.combineMessageListener.onErrorMessage(_VENDER.name(), connid, 
                    InBoxMessage.getMessage(connid, _VENDER, InBoxMessage.ERROR, lineObject, config, _times), receiveTime);
            return;
        }
        if (lineObject.has("type") && lineObject.getString("type").equals("PRICE")) {
            _times.put("timeB", Instant.now().toEpochMilli()); // 处理时间
            this.combineMessageListener.onPricingSteamMessage(_VENDER.name(), connid, 
                    InBoxMessage.getMessage(connid, _VENDER, InBoxMessage.PRICE_STREAM, lineObject, _times), receiveTime);
            return;
        }
        if (lineObject.has("type") && lineObject.getString("type").equals("HEARTBEAT")) {
            _times.put("timeB", Instant.now().toEpochMilli()); // 处理时间
            this.combineMessageListener.onPingMessage(_VENDER.name(), connid, 
                    InBoxMessage.getMessage(connid, _VENDER, InBoxMessage.PING, lineObject, _times), receiveTime);
            return;
        }
        logger.warn("收到一条消息未处理, url={}, message={}", request.url(), lineObject);
    }

    public void onDisconnectTrigger(Request request, Exception e) {
        Long eventTime = Instant.now().toEpochMilli();
        JSONObject _times = new JSONObject();
        _times.put("timeA", eventTime); // 收到时间
        JSONObject lineObject = new JSONObject();
        lineObject.put("errorMessage", e.getMessage());
        combineMessageListener.onFailed(_VENDER.name(), this.connid, 
                InBoxMessage.getMessage(connid, _VENDER, InBoxMessage.FAILED, lineObject, null, _times), eventTime);
    }

    public void onOpenTrigger(Request request) {
        JSONObject _times = new JSONObject();
        _times.put("timeA", Instant.now().toEpochMilli()); // 收到时间
        JSONObject lineObject = new JSONObject();
        lineObject.put("connid", connid);
        lineObject.put("connectPrimary", "pricing-stream"); // 一级连接类型
        lineObject.put("desc", "外汇市场价格变动连接建立成功,即将启动价格流");
        combineMessageListener.onConnected(_VENDER.name(), this.connid, 
                InBoxMessage.getMessage(connid, _VENDER, InBoxMessage.FAILED, lineObject, _times));
    }
    
}
