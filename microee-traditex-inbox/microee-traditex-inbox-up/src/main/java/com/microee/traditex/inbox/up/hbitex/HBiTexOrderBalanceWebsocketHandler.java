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

public class HBiTexOrderBalanceWebsocketHandler implements HttpWebsocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(HBiTexOrderBalanceWebsocketHandler.class);
    public static final String _VENDER = VENDER.HBiTex.name();

    private WebSocket webSocket;
    private final String connid;
    private ConnectStatus connectStatus;
    private final CombineMessageListener combineMessageListener;

    public HBiTexOrderBalanceWebsocketHandler(final String connid, final CombineMessageListener combineMessageListener) {
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
        this.combineMessageListener.onFailed(_VENDER, connid, 
                InBoxMessage.getMessage(connid, VENDER.HBiTex, InBoxMessage.FAILED, jsonObject, _times), receiveTime);
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
        this.combineMessageListener.onTimeout(_VENDER, connid, 
                InBoxMessage.getMessage(connid, VENDER.HBiTex, InBoxMessage.TIMEOUT, jsonObject, _times), receiveTime, start, end);
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
            jsonObject.put("connectPrimary", "orderbalance"); // 一级连接类型
            jsonObject.put("url", webSocket.request().url());
            jsonObject.put("headers", webSocket.request().headers());
            jsonObject.put("uid", webSocket.request().headers().get("uid"));
            this.combineMessageListener.onConnected(_VENDER, connid, 
                    InBoxMessage.getMessage(connid, VENDER.HBiTex, InBoxMessage.CONNECTED, jsonObject, _times));
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
        if (jsonObject.has("op") && jsonObject.getString("op").equals("ping")) {
            this.writeMessageWithoutLog(String.format("{\"op\":\"pong\", \"ts\":%s}", jsonObject.getLong("ts")));
            jsonObject.put("event", "ping");
            this.combineMessageListener.onPingMessage(_VENDER, connid, 
                    InBoxMessage.getMessage(connid, VENDER.HBiTex, InBoxMessage.PING, jsonObject, _times), receiveTime);
            return;
        }
        if (jsonObject.has("op") && jsonObject.getString("op").equals("close")) {
            logger.warn("连接被动关闭, connid={}, url={}, message={}", connid, webSocket.request().url().toString(), jsonMessage);
            return;
        }
        if (jsonObject.has("op") && jsonObject.getString("op").equals("auth")) {
            jsonObject.put("timeB", Instant.now().toEpochMilli()); // 处理时间
            this.combineMessageListener.onAuthMessage(_VENDER, connid, 
                    InBoxMessage.getMessage(connid, VENDER.HBiTex, InBoxMessage.AUTH, jsonObject, _times), receiveTime);
            return;
        }
        if (jsonObject.has("op")) {
            if (jsonObject.getString("op").equals("sub")) {
                String _topic = jsonObject.getString("topic");
                if (_topic.equals("accounts")) {
                    jsonObject.put("timeB", Instant.now().toEpochMilli()); // 处理时间
                    this.combineMessageListener.onSubbedMessage(_VENDER, connid, 
                            InBoxMessage.getMessage(connid, VENDER.HBiTex, InBoxMessage.SUBBED, jsonObject, _times), _topic, receiveTime);
                    return;
                }
                if (_topic.matches("orders.[a-z]{1,}.update")) {
                    jsonObject.put("timeB", Instant.now().toEpochMilli()); // 处理时间
                    this.combineMessageListener.onSubbedMessage(_VENDER, connid, 
                            InBoxMessage.getMessage(connid, VENDER.HBiTex, InBoxMessage.SUBBED, jsonObject, _times), _topic, receiveTime);
                    return;
                }
            }
            if (jsonObject.getString("op").equals("unsub")) {
                String _topic = jsonObject.getString("topic");
                jsonObject.put("timeB", Instant.now().toEpochMilli()); // 处理时间
                this.combineMessageListener.onUnSubbedMessage(_VENDER, connid,
                        InBoxMessage.getMessage(connid, VENDER.HBiTex, InBoxMessage.UNSUB, jsonObject, _times), _topic);
                return;
            }
        }
        if (jsonObject.has("op") && jsonObject.getString("op").equals("notify")) {
            if (jsonObject.has("topic")) {
                if (jsonObject.getString("topic").equals("accounts")) {
                    jsonObject.put("timeB", Instant.now().toEpochMilli()); // 处理时间
                    this.combineMessageListener.onAccountMessage(_VENDER, connid, 
                            InBoxMessage.getMessage(connid, VENDER.HBiTex, InBoxMessage.ACCOUNT_CHANGE, jsonObject, _times), receiveTime);
                    return;
                }
                if (jsonObject.getString("topic").matches("orders.[a-z]{1,}.update")) {
                    jsonObject.put("timeB", Instant.now().toEpochMilli()); // 处理时间
                    this.combineMessageListener.onOrderStatMessage(_VENDER, connid, 
                            InBoxMessage.getMessage(connid, VENDER.HBiTex, InBoxMessage.ORDER_STATS, jsonObject, _times), receiveTime);
                    return;
                }
            }
        }
        if (jsonObject.has("err-code") && jsonObject.getInt("err-code") != 0) {
            jsonObject.put("timeB", Instant.now().toEpochMilli()); // 处理时间
            jsonObject.put("url", webSocket.request().url().toString());
            jsonObject.put("headers", webSocket.request().headers());
            this.combineMessageListener.onErrorMessage(_VENDER, connid, 
                    InBoxMessage.getMessage(connid, VENDER.HBiTex, InBoxMessage.ERROR, jsonObject, _times), receiveTime);
            return;
        }
        jsonObject.put("timeB", Instant.now().toEpochMilli()); // 处理时间
        jsonObject.put("url", webSocket.request().url().toString());
        jsonObject.put("headers", webSocket.request().headers());
        jsonObject.put("desc", "收到一条消息未处理");
        this.combineMessageListener.onErrorMessage(_VENDER, connid, 
                InBoxMessage.getMessage(connid, VENDER.HBiTex, InBoxMessage.UNKNOW, jsonObject, _times), receiveTime);
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
        return connectStatus;
    }

    public void setConnectStatus(ConnectStatus connectStatus) {
        this.connectStatus = connectStatus;
    }

}
