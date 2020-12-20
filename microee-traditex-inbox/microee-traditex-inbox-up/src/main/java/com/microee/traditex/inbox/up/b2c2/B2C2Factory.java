package com.microee.traditex.inbox.up.b2c2;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.ImmutableList;
import com.microee.plugin.http.assets.HttpAssets;
import com.microee.plugin.http.assets.HttpClient;
import com.microee.plugin.http.assets.HttpClientResult;
import com.microee.plugin.http.assets.HttpWebsocketListener;
import com.microee.plugin.response.R;
import com.microee.plugin.response.exception.RestException;
import com.microee.traditex.inbox.oem.connector.ITridexTradFactory;
import com.microee.traditex.inbox.oem.constrants.ConnectStatus;
import com.microee.traditex.inbox.oem.constrants.VENDER;
import com.microee.traditex.inbox.oem.hbitex.ConnectType;
import com.microee.traditex.inbox.up.CombineMessageListener;
import com.microee.traditex.inbox.up.TradiTexAssists;
import okhttp3.Headers;
import okhttp3.WebSocket;

public class B2C2Factory implements ITridexTradFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(B2C2Factory.class);

    private final String connid;
    private final String wsHost;
    private final String restHost;
    private final Headers headers;
    private final Headers wsHeaders;
    private final JSONObject auth;
    private final ConnectType connectType;
    private final HttpClient httpClient;
    private final HttpClient httpClientStream;
    private final B2C2OrderBookWebsocketHandler wsOrderBookHandler;
    private final B2C2OrderBookThread orderBookThread;
    private final List<String> topics;
    private Long lastTime;
    private String lastEvent;
    private Long lastPing; // 最后一次续约时间
    private Long lease; // 租约有效期, 超过这个时间将被销毁
    private final B2C2Proxy proxy;

    public B2C2Factory(String connid, String wsHost, String restHost, String accessToken, ConnectType connectType,
            CombineMessageListener combineMessageListener, InetSocketAddress proxyAddress) {
        this.connid = connid;
        this.wsHost = wsHost;
        this.restHost = restHost;
        this.headers = Headers.of("connid", connid, "Authorization", String.format("Token %s", accessToken));
        this.wsHeaders = Headers.of("connid", connid, "Authorization", String.format("Token %s", accessToken), "Connection", "Upgrade", "Upgrade", "websocket");
        this.auth = this.authentication(accessToken);
        this.connectType = connectType;
        this.httpClientStream = HttpClient.create(null, proxyAddress);
        this.httpClient = HttpClient.create(null, proxyAddress);
        this.wsOrderBookHandler =
                new B2C2OrderBookWebsocketHandler(this.connid, combineMessageListener);
        this.orderBookThread = new B2C2OrderBookThread(this);
        this.topics = new ArrayList<>();
        this.lastEvent = null;
        this.lastTime = null;
        this.lastPing = null;
        this.lease = null;
        this.proxy = new B2C2Proxy(this);
    }

    public WebSocket createOrderBookStream() {
        try {
            WebSocket socket =
                    this.httpClientStream.websocket(this.getHttpUrl(this.wsHost, "/quotes"),
                            this.wsHeaders, new HttpWebsocketListener(this.wsOrderBookHandler));
            return socket;
        } catch (Exception e) {
            LOGGER.error("b2c2建立websocket连接异常: errorMessage={}", e.getMessage(), e);
            return null;
        }
    }

    public <T> T get(String memo, String endpoint, Map<String, Object> queryParams,
            TypeReference<T> typeRef) {
        final String connectorString = this.getHttpUrl(this.restHost, endpoint);
        HttpClientResult httpClientResult = null;
        httpClientResult = this.httpClient.doGetWithQueryParams(connectorString, this.headers, queryParams);
        if (httpClientResult == null) {
            throw new RestException(R.TIME_OUT, "超时");
        }
        if (!httpClientResult.isSuccess()) {
            throw new RestException(R.FAILED, HttpAssets.toJsonString(httpClientResult));
        }
        return HttpAssets.parseJson(httpClientResult.getResult(), typeRef);
    }
    public <T> T post(String memo, String endpoint, Map<String, Object> body, TypeReference<T> typeRef) {
        final String connectorString = this.getHttpUrl(this.restHost, endpoint);
        HttpClientResult httpClientResult = this.httpClient.postJson(connectorString, this.headers, body);
        if (httpClientResult == null) {
            throw new RestException(R.TIME_OUT, "超时");
        }
        if (!httpClientResult.isSuccess()) {
            throw new RestException(R.FAILED, HttpAssets.toJsonString(httpClientResult));
        }
        return HttpAssets.parseJson(httpClientResult.getResult(), typeRef);
    }

    public String getHttpUrl(String host, String endpoint) {
        return String.join("/", host.replaceFirst("(/+)$", ""), endpoint.replaceFirst("^(/+)", ""));
    }

    @Override
    public Object factory() {
        return this;
    }

    @Override
    public String connid() {
        return this.headers.get("connid");
    }

    @Override
    public JSONObject config() {
        JSONObject json = new JSONObject();
        json.put("vender", VENDER.B2C2);
        json.put("connectType", this.connectType);
        json.put("headers", this.readHeaders());
        json.put("wsHost", this.wsHost);
        json.put("topic", this.topics());
        json.put("lastEvent",
                this.lastEvent == null ? "N/a" : (this.lastEvent + "/" + this.lastTime));
        json.put("lastPing", this.lastPing == null ? "N/a" : (this.lastPing + "/" + this.lease));
        JSONObject authjson = new JSONObject();
        if (this.auth != null && this.auth.has("accessToken")) {
            authjson.put("accessToken",
                    TradiTexAssists.shorterContent(this.auth.getString("accessToken"), (short) 8));
        }
        json.put("auth", authjson);
        json.put("status", this.wsOrderBookHandler.getConnectStatus().name());
        return json;
    }

    public JSONObject authentication(String accessToken) {
        return new JSONObject().put("accessToken", accessToken);
    }

    @Override
    public ConnectStatus status() {
        if (this.wsOrderBookHandler == null) {
            return null;
        }
        return this.wsOrderBookHandler.getConnectStatus();
    }

    public List<String> readHeaders() {
        return ImmutableList.copyOf(this.headers.iterator()).stream()
                .map(f -> String.format("%s: %s", f.getFirst(),
                        TradiTexAssists.shorterContent(f.getSecond(), (short) 15)))
                .collect(Collectors.toList());
    }

    @Override
    public String genOTP(String connid) {
        if (this.config().has("auth")) {
            JSONObject auth = this.config().getJSONObject("auth");
            if (auth.has("accessToken")) {
                return _otp(connid, auth.getString("accessToken"));
            }
        }
        return null;
    }

    @Override
    public Boolean otp(String connid, String otp) {
        if (this.config().has("auth")) {
            JSONObject auth = this.config().getJSONObject("auth");
            if (auth.has("accessToken")) {
                String theOtp = _otp(connid, auth.getString("accessToken"));
                return theOtp.equals(otp);
            }
        }
        return false;
    }

    private String _otp(String connid, String secret) {
        return DigestUtils.md5Hex(String.format("%s:%s", connid, secret));
    }

    @Override
    public void shutdown() {
        LOGGER.info("b2c2-连接已销毁: connid={}, status={}, connectConfig={}", this.connid(),
                this.wsOrderBookHandler.getConnectStatus(), this.config());
        this.wsOrderBookHandler.closeWebsocket();
        this.orderBookThread.shutdown();
    }

    @Override
    public void connect(boolean retryer) {
        if (retryer) {
            LOGGER.info("b2c2启动重连: connid={}, connectConfig={}", this.connid(), this.config());
            this.orderBookThread.start();
            return;
        }
        LOGGER.info("b2c2建立连接: connid={}, connectConfig={}", this.connid(), this.config());
        this.orderBookThread.start();
    }

    @Override
    public List<String> topics() {
        return this.topics;
    }

    @Override
    public void addTopic(String topic) {
        if (!this.topics.contains(topic)) {
            this.topics.add(topic);
        }
    }

    @Override
    public boolean removeTopics(String topic) {
        this.topics.remove(topic);
        return true;
    }

    @Override
    public ITridexTradFactory putLastTime(Long timestamp) {
        this.lastTime = timestamp;
        return this;
    }

    @Override
    public ITridexTradFactory putLastEvent(String event) {
        this.lastEvent = event;
        return this;
    }

    @Override
    public void putLastPing(long epochMilli, long livetime) {
        this.lastPing = epochMilli;
        this.lease = this.lastPing + livetime;
    }

    @Override
    public Long lease() {
        return this.lease;
    }

    public B2C2OrderBookWebsocketHandler orderBookStreamHandler() {
        return this.wsOrderBookHandler;
    }

    public B2C2Proxy proxy() {
        return this.proxy;
    }

}
