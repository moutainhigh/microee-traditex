package com.microee.traditex.inbox.up.hbitex;

import java.time.Instant;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.microee.plugin.http.assets.HttpWebsocketHandler;
import com.microee.plugin.zip.Zip;
import com.microee.traditex.inbox.oem.constrants.ConnectStatus;
import com.microee.traditex.inbox.oem.constrants.VENDER;
import com.microee.traditex.inbox.up.CombineMessageListener;
import com.microee.traditex.inbox.up.InBoxMessage;
import okhttp3.Response;
import okhttp3.WebSocket;
import okio.ByteString;

public class HBiTexOrderBookWebsocketHandler implements HttpWebsocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(HBiTexOrderBookWebsocketHandler.class);
    public static final VENDER _VENDER = VENDER.HBiTex;

    private final String connid;
    private WebSocket webSocket;
    private ConnectStatus connectStatus;
    private final CombineMessageListener combineMessageListener;

    public HBiTexOrderBookWebsocketHandler(final String connid, final CombineMessageListener combineMessageListener) {
        this.connid = connid;
        this.combineMessageListener = combineMessageListener;
    }

    @Override
    public void onClosedHandler(WebSocket webSocket, int code, String reason) {
        this.connectStatus = ConnectStatus.CLOSED;
        logger.info("websocket连接已关闭: url={}, code={}, reason={}", webSocket.request().url().toString(), code, reason);
    }

    @Override
    public void onFailureHandler(WebSocket webSocket, Throwable t, String responseText) {
        if (this.connectStatus != null && this.connectStatus.equals(ConnectStatus.DESTROY)) {
            // 主动销毁
            return;
        }
        this.connectStatus = ConnectStatus.DAMAGED;
        logger.error("onFailureHandler, status={}, errorMessage={}", this.connectStatus, responseText);
        Long receiveTime = Instant.now().toEpochMilli();
        JSONObject _times = new JSONObject();
        _times.put("timeA", receiveTime); // 收到时间
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("url", webSocket.request().url());
        jsonObject.put("headers", webSocket.request().headers());
        jsonObject.put("errorMessage", responseText);
        this.combineMessageListener.onFailed(_VENDER.name(), connid, 
                InBoxMessage.getMessage(connid, _VENDER, InBoxMessage.FAILED, jsonObject, _times), receiveTime);
    }

    @Override
    public void onTimeoutHandler(String url, long start, long end) {
        this.connectStatus = ConnectStatus.TIMEOUT;
        Long receiveTime = Instant.now().toEpochMilli();
        JSONObject _times = new JSONObject();
        _times.put("timeA", receiveTime); // 收到时间
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("start", start);
        jsonObject.put("end", end);
        jsonObject.put("duration", end - start);
        jsonObject.put("url", url);
        this.combineMessageListener.onTimeout(_VENDER.name(), connid, 
                InBoxMessage.getMessage(connid, _VENDER, InBoxMessage.TIMEOUT, jsonObject, _times), receiveTime, start, end);
    }
    
    @Override
    public void onOpenHandler(WebSocket webSocket, Response response) {
        this.connectStatus = ConnectStatus.ONLINE;
        this.webSocket = webSocket;
        if (this.combineMessageListener != null) {
            Long receiveTime = Instant.now().toEpochMilli();
            JSONObject _times = new JSONObject();
            _times.put("timeA", receiveTime); // 收到时间
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("connectPrimary", "orderbook"); // 一级连接类型
            jsonObject.put("url", webSocket.request().url());
            jsonObject.put("headers", webSocket.request().headers());
            this.combineMessageListener.onConnected(_VENDER.name(), connid, 
                    InBoxMessage.getMessage(connid, _VENDER, InBoxMessage.CONNECTED, jsonObject, _times));
        }
    }

    @Override
    public void onMessageStringHandler(WebSocket webSocket, String text) {
        logger.info("onMessageStringHandler, text={}", text);
    }

    @Override
    public void onMessageByteStringHandler(WebSocket webSocket, ByteString bytes) {
        String jsonMessage = Zip.ungzip(bytes.toByteArray());
        Long receiveTime = Instant.now().toEpochMilli();
        JSONObject _times = new JSONObject();
        _times.put("timeA", receiveTime); // 收到时间
        JSONObject jsonObject = new JSONObject(jsonMessage);
        jsonObject.put("vender", _VENDER);
        if (jsonObject.has("ping")) {
            this.writeMessageWithoutLog(String.format("{\"pong\": %s}", jsonObject.getLong("ping")));
            _times.put("timeB", Instant.now().toEpochMilli()); // 处理时间
            this.combineMessageListener.onPingMessage(_VENDER.name(), connid, 
                    InBoxMessage.getMessage(connid, _VENDER, InBoxMessage.PING, jsonObject, _times), receiveTime);
            return;
        }
        if (jsonObject.has("ch") && jsonObject.has("tick") // 深度
                && jsonObject.getString("ch").startsWith("market.")) {
            _times.put("timeB", Instant.now().toEpochMilli()); // 处理时间
            this.combineMessageListener.onOrderBookMessage(_VENDER.name(), connid, 
                    InBoxMessage.getMessage(connid, _VENDER, InBoxMessage.QUOTE, jsonObject, _times), jsonObject.getLong("ts"));
            return;
        }
        if (jsonObject.has("subbed")) {
            String _topic = jsonObject.getString("subbed");
            _times.put("timeB", Instant.now().toEpochMilli()); // 处理时间
            this.combineMessageListener.onSubbedMessage(_VENDER.name(), connid,
                    InBoxMessage.getMessage(connid, _VENDER, InBoxMessage.SUBBED, jsonObject, _times), _topic, receiveTime);
            return;
        }
        if (jsonObject.has("unsubbed")) {
            String _topic = jsonObject.getString("unsubbed");
            _times.put("timeB", Instant.now().toEpochMilli()); // 处理时间
            this.combineMessageListener.onUnSubbedMessage(_VENDER.name(), connid,
                    InBoxMessage.getMessage(connid, _VENDER, InBoxMessage.UNSUBBED, jsonObject, _times), _topic);
            return;
        }
        if (jsonObject.has("err-code")) {
            _times.put("timeB", Instant.now().toEpochMilli()); // 处理时间
            jsonObject.put("url", webSocket.request().url().toString());
            jsonObject.put("headers", webSocket.request().headers());
            this.combineMessageListener.onErrorMessage(_VENDER.name(), connid, 
                    InBoxMessage.getMessage(connid, _VENDER, InBoxMessage.ERROR, jsonObject, _times), receiveTime);
            return;
        }
        jsonObject.put("timeB", Instant.now().toEpochMilli()); // 处理时间
        jsonObject.put("url", webSocket.request().url().toString());
        jsonObject.put("headers", webSocket.request().headers());
        jsonObject.put("desc", "收到一条消息未处理");
        this.combineMessageListener.onErrorMessage(_VENDER.name(), connid, 
                InBoxMessage.getMessage(connid, _VENDER, InBoxMessage.UNKNOW, jsonObject, _times), receiveTime);
    }

    @Override
    public void closeWebsocket() {
        if (this.webSocket != null) {
            try {
                this.connectStatus = ConnectStatus.DESTROY;
                // https://tools.ietf.org/html/rfc6455#section-7.4
                boolean b = this.webSocket.close(1000, "主动关闭连接");
                logger.info("连接已销毁: connid={}, closed={}", this.connid, b);
            } catch (Exception e) {
                logger.info("主动关闭连接异常: connid={}, errorMessage={}", this.connid, e.getMessage(), e);
            }
        }
    }

    public void writeMessageWithoutLog(String message) {
        this.webSocket.send(message);
    }
    
    public void writeMessage(String message) {
        this.webSocket.send(message);
        logger.info("发送了一条消息: connid={}, url={}, message={}", this.connid, webSocket.request().url(), message);
    }

    public ConnectStatus getConnectStatus() {
        return this.connectStatus == null ? ConnectStatus.UNKNOW : this.connectStatus;
    }

    public void setConnectStatus(ConnectStatus connectStatus) {
        this.connectStatus = connectStatus;
    }

}
