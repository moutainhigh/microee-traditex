package com.microee.traditex.inbox.up.cumberland;

import java.time.Instant;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.microee.plugin.http.assets.HttpAssets;
import com.microee.plugin.http.assets.HttpWebsocketHandler;
import com.microee.traditex.inbox.oem.constrants.ConnectStatus;
import com.microee.traditex.inbox.oem.constrants.VENDER;
import com.microee.traditex.inbox.oem.cumberland.entity.CBAsyncResult;
import com.microee.traditex.inbox.oem.cumberland.entity.CBExternalIdentity;
import com.microee.traditex.inbox.oem.cumberland.wsmessage.CBTradeHistoryResponseEvent;
import com.microee.traditex.inbox.oem.cumberland.wsmessage.CBTradeResponseEvent;
import com.microee.traditex.inbox.oem.cumberland.wsmessage.StreamEventBase;
import com.microee.traditex.inbox.oem.cumberland.wsmessage.StreamQuoteEvent;
import com.microee.traditex.inbox.up.CombineMessageListener;
import com.microee.traditex.inbox.up.InBoxMessage;
import okhttp3.Response;
import okhttp3.WebSocket;
import okio.ByteString;

public class CumberLandOrderBookWebsocketHandler implements HttpWebsocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(CumberLandOrderBookWebsocketHandler.class);
    public static final VENDER _VENDER = VENDER.CumberLand;

    private final String connid;
    private static final ConcurrentHashMap<String, CBAsyncResult<?>> _MESSAGE_MAP = new ConcurrentHashMap<>();
    private WebSocket webSocket;
    private ConnectStatus connectStatus;
    private final CombineMessageListener combineMessageListener;
    private final String userId;

    public void sendMessage(String jsonMessage) {
        if (this.webSocket == null) {
            throw new RuntimeException("cumberland-websocket-连接尚未打开");
        }
        this.webSocket.send(jsonMessage);
    }
    
    /**
     * 发送消息，发送并接到回复才返回
     */
    @SuppressWarnings("unchecked")
    public <T> T sendMessageSync(String message, String requestId, CBAsyncResult<T> asyncResult) {
        if (this.webSocket == null) {
            throw new RuntimeException("cumberland-websocket-连接尚未打开");
        }
        // 放入队列等待回复
        _MESSAGE_MAP.put(requestId, asyncResult);
        this.webSocket.send(message);
        logger.info("cumberland发送ws消息成功: message={}, requestId={}", message, requestId);
        try {
            // 最多等3秒, 3秒无回复放弃
            if (asyncResult.await(8)) { 
                asyncResult = (CBAsyncResult<T>) _MESSAGE_MAP.get(requestId);
                return asyncResult.getResult();
            } else {
                logger.warn("cumberland-3秒内没拿到消息回复: requestId={}", requestId);
                return null;
            }
        } catch (Exception e) {
            logger.error("cumberland发送异步消息异常: errorMessage={}", e.getMessage(), e);
        } finally {
            // 用完移除
            _MESSAGE_MAP.remove(requestId);
        }
        return null;
    }

    public CumberLandOrderBookWebsocketHandler(final String connid, final String userId,
            final CombineMessageListener combineMessageListener) {
        this.connid = connid;
        this.userId = userId;
        this.combineMessageListener = combineMessageListener;
    }

    @Override
    public void onClosedHandler(WebSocket webSocket, int code, String reason) {
        this.connectStatus = ConnectStatus.CLOSED;
        logger.info("websocket连接已关闭: connid={}, url={}, code={}, reason={}", connid, webSocket.request().url().toString(), code, reason);
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
        JSONObject _times = new JSONObject();
        Long receiveTime = Instant.now().toEpochMilli();
        _times.put("timeA", receiveTime); // 收到时间
        JSONObject jsonObject = new JSONObject(line);
        StreamEventBase streamEvent = HttpAssets.parseJson(line, StreamEventBase.class);
        if (streamEvent.getMessageType().equals("ERROR_RESPONSE")) {
            logger.error("cumberland收到一条错误消息推送: message={}", line);
            return;
        }
        if (streamEvent.getMessageType().equals("STREAMING_QUOTE_RESPONSE")) {
            StreamQuoteEvent streamQuoteEvent = HttpAssets.parseJson(line, StreamQuoteEvent.class);
            if (streamQuoteEvent.getReason() != null && streamQuoteEvent.getReason().equals("Filled")) {
                // 订单成交
                return;
            }
            if (_MESSAGE_MAP.containsKey(streamQuoteEvent.getCounterpartyRequestId())) {
                _MESSAGE_MAP.get(streamQuoteEvent.getCounterpartyRequestId()).setResult(streamQuoteEvent).success();
                if (streamQuoteEvent.getCounterpartyRequestId().startsWith("TRAD-QUOTE-")) {
                    // ??? 需确认下单完成后该报价是否还继续推送，如继续推送需关闭
                    return;
                }
                jsonObject.put("timeB", Instant.now().toEpochMilli()); // 处理时间
                logger.info("cumberland订阅orderbook成功: msgType={}, status={}, message={}", streamEvent.getMessageType(), streamEvent.getStatus(), line);
                this.combineMessageListener.onSubbedMessage(_VENDER.name(), connid, 
                        InBoxMessage.getMessage(connid, _VENDER, InBoxMessage.SUBBED, jsonObject, _times), streamQuoteEvent.getQuoteRequest().getTicker(), receiveTime);
                return;
            }
            if (!this.validate(streamQuoteEvent.getExternalIdentity(), this.userId, line)) {
                return;
            }
            // orderbook stream
            _times.put("timeB", Instant.now().toEpochMilli()); // 处理时间
            this.combineMessageListener.onOrderBookMessage(_VENDER.name(), connid, 
                    InBoxMessage.getMessage(connid, _VENDER, InBoxMessage.QUOTE, jsonObject, _times), receiveTime);
            return;
        }
        if (streamEvent.getMessageType().equals("TRADE_HISTORY_RESPONSE")) {
            CBTradeHistoryResponseEvent streamQuoteEvent = HttpAssets.parseJson(line, CBTradeHistoryResponseEvent.class);
            if (_MESSAGE_MAP.containsKey(streamQuoteEvent.getCounterpartyRequestId())) {
                _MESSAGE_MAP.get(streamQuoteEvent.getCounterpartyRequestId()).setResult(streamQuoteEvent).success();
                return;
            }
        }
        if (streamEvent.getMessageType().equals("TRADE_RESPONSE")) {
            // 收到订单状态推送
            CBTradeResponseEvent tradDetails = HttpAssets.parseJson(line, CBTradeResponseEvent.class);
            if (!this.validate(tradDetails.getExternalIdentity(), this.userId, line)) {
                return;
            }
            jsonObject.put("timeB", Instant.now().toEpochMilli()); // 处理时间
            if (_MESSAGE_MAP.containsKey(tradDetails.getTradeRequest().getCounterpartyRequestId())) {
                    _MESSAGE_MAP.get(tradDetails.getTradeRequest().getCounterpartyRequestId()).setResult(tradDetails).success();
            }
            this.combineMessageListener.onOrderStatMessage(_VENDER.name(), connid, jsonObject, receiveTime);
            return;
        }
        logger.warn("收到一条消息未处理, vender={}, url={}, message={}", _VENDER.name(), webSocket.request().url().toString(), line);
    }

    public boolean validate(CBExternalIdentity identity, String userid, String line) {
        if (identity == null || identity.getUserId() == null) {
            return false;
        }
        if (!userid.equals(identity.getUserId())) {
            // 账号不匹配忽略
            logger.warn("cumberland账号不匹配忽略: message={}", line);
            return false;
        }
        return true;
    }
    
    public Set<String> keys(Enumeration<String> e) {
        Set<String> set = new HashSet<>();
        while(e.hasMoreElements()) {
            set.add(e.nextElement());
        }
        return set;
    }

    @Override
    public void onMessageByteStringHandler(WebSocket webSocket, ByteString bytes) {

    }

    // https://tools.ietf.org/html/rfc6455#section-7.4
    @Override
    public void closeWebsocket() {
        if (this.webSocket != null) {
            try {
                this.connectStatus = ConnectStatus.DESTROY;
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
