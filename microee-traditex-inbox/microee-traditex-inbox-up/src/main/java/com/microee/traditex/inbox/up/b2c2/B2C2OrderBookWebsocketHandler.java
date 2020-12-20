package com.microee.traditex.inbox.up.b2c2;

import java.time.Instant;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.microee.plugin.http.assets.HttpAssets;
import com.microee.plugin.http.assets.HttpWebsocketHandler;
import com.microee.traditex.inbox.oem.b2c2.wsmessage.BCEventBase;
import com.microee.traditex.inbox.oem.constrants.ConnectStatus;
import com.microee.traditex.inbox.oem.constrants.VENDER;
import com.microee.traditex.inbox.up.CombineMessageListener;
import com.microee.traditex.inbox.up.InBoxMessage;
import okhttp3.Response;
import okhttp3.WebSocket;
import okio.ByteString;

public class B2C2OrderBookWebsocketHandler implements HttpWebsocketHandler {

    private static final Logger logger =
            LoggerFactory.getLogger(B2C2OrderBookWebsocketHandler.class);
    public static final VENDER _VENDER = VENDER.B2C2;

    private final String connid;
    private WebSocket webSocket;
    private ConnectStatus connectStatus;
    private final CombineMessageListener combineMessageListener;

    public void sendMessage(String message) {
        if (this.webSocket == null) {
            throw new RuntimeException("b2c2websocket连接尚未打开");
        }
        this.webSocket.send(message);
        logger.info("b2c2发送了一个消息: message={}", message);
    }
    
    public B2C2OrderBookWebsocketHandler(final String connid,
            final CombineMessageListener combineMessageListener) {
        this.connid = connid;
        this.combineMessageListener = combineMessageListener;
    }

    @Override
    public void onClosedHandler(WebSocket webSocket, int code, String reason) {
        this.connectStatus = ConnectStatus.CLOSED;
        logger.info("b2c2websocket连接已关闭: connid={}, url={}, code={}, reason={}", connid,
                webSocket.request().url().toString(), code, reason);
    }

    @Override
    public void onFailureHandler(WebSocket webSocket, Throwable t, String responseText) {
        if (this.connectStatus != null && this.connectStatus.equals(ConnectStatus.DESTROY)) {
            // 主动销毁
            return;
        }
        this.connectStatus = ConnectStatus.DAMAGED;
        logger.error("onFailureHandler, status={}, errorMessage={}", this.connectStatus,
                responseText);
        Long receiveTime = Instant.now().toEpochMilli();
        JSONObject _times = new JSONObject();
        _times.put("timeA", receiveTime); // 收到时间
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("url", webSocket.request().url());
        jsonObject.put("headers", webSocket.request().headers());
        jsonObject.put("errorMessage", responseText);
        this.combineMessageListener.onFailed(_VENDER.name(), connid,
                InBoxMessage.getMessage(connid, _VENDER, InBoxMessage.FAILED, jsonObject, _times),
                receiveTime);
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
                InBoxMessage.getMessage(connid, _VENDER, InBoxMessage.TIMEOUT, jsonObject, _times),
                receiveTime, start, end);
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
            this.combineMessageListener.onConnected(_VENDER.name(), connid, InBoxMessage
                    .getMessage(connid, _VENDER, InBoxMessage.CONNECTED, jsonObject, _times));
        }
    }

    @Override
    public void onMessageStringHandler(WebSocket webSocket, String line) {
        Long receiveTime = Instant.now().toEpochMilli();
        JSONObject _times = new JSONObject();
        _times.put("timeA", receiveTime); // 收到时间
        JSONObject jsonObject = new JSONObject(line);
        BCEventBase event = HttpAssets.parseJson(line, new TypeReference<BCEventBase>() {});
        if (event == null) {
            return;
        }
        if (event.getEvent().equals("tradable_instruments")) {
            // 验证通过后返回所有可订阅的交易对
            _times.put("timeB", Instant.now().toEpochMilli()); // 处理时间
            logger.info("b2c2连接成功: event={}, success={}, message={}", event.getEvent(), event.getSuccess(), line);
            return;
        } 
        if (event.getEvent().equals("price")) {
            // orderbook
            _times.put("timeB", Instant.now().toEpochMilli()); // 处理时间
            this.combineMessageListener.onOrderBookMessage(_VENDER.name(), connid, 
                    InBoxMessage.getMessage(connid, _VENDER, InBoxMessage.QUOTE, jsonObject, _times), receiveTime);
            return;
        } 
        if (event.getEvent().equals("subscribe")) {
            // orderbook
            _times.put("timeB", Instant.now().toEpochMilli()); // 处理时间
            this.combineMessageListener.onSubbedMessage(_VENDER.name(), connid, 
                    InBoxMessage.getMessage(connid, _VENDER, InBoxMessage.QUOTE, jsonObject, _times), jsonObject.getString("instrument"), receiveTime);
            return;
        } 
        if (event.getEvent().equals("unsubscribe")) {
            // orderbook
            _times.put("timeB", Instant.now().toEpochMilli()); // 处理时间
            this.combineMessageListener.onUnSubbedMessage(_VENDER.name(), connid, 
                    InBoxMessage.getMessage(connid, _VENDER, InBoxMessage.QUOTE, jsonObject, _times), jsonObject.getString("instrument"));
            return;
        } 
        logger.warn("收到一条消息未处理, vender={}, url={}, message={}", _VENDER.name(),
                webSocket.request().url().toString(), line);
    }

    @Override
    public void onMessageByteStringHandler(WebSocket webSocket, ByteString bytes) {

    }

    @Override
    public void closeWebsocket() {
        if (this.webSocket != null) {
            try {
                this.connectStatus = ConnectStatus.DESTROY;
                // https://tools.ietf.org/html/rfc6455#section-7.4
                this.webSocket.close(1000, "主动关闭连接");
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
        logger.info("发送了一条消息: connid={}, url={}, message={}", this.connid,
                webSocket.request().url(), message);
    }

    public ConnectStatus getConnectStatus() {
        return this.connectStatus == null ? ConnectStatus.UNKNOW : this.connectStatus;
    }

    public void setConnectStatus(ConnectStatus connectStatus) {
        this.connectStatus = connectStatus;
    }

}
