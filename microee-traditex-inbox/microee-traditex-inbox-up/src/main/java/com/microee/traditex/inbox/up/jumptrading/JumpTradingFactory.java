package com.microee.traditex.inbox.up.jumptrading;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.ImmutableList;
import com.microee.plugin.http.assets.HttpAssets;
import com.microee.plugin.http.assets.HttpClient;
import com.microee.plugin.http.assets.HttpClientFactory;
import com.microee.plugin.http.assets.HttpClientResult;
import com.microee.plugin.response.R;
import com.microee.plugin.response.exception.RestException;
import com.microee.traditex.inbox.oem.connector.ITridexTradFactory;
import com.microee.traditex.inbox.oem.constrants.ConnectStatus;
import com.microee.traditex.inbox.oem.constrants.VENDER;
import com.microee.traditex.inbox.oem.hbitex.ConnectType;
import com.microee.traditex.inbox.up.CombineMessageListener;
import com.microee.traditex.inbox.up.TradiTexAssists;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class JumpTradingFactory implements ITridexTradFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(JumpTradingFactory.class);

    private String streamHost;
    private Headers headers;
    private final JSONObject auth;
    private final ConnectType connectType;
    private final HttpClient httpClient;
    private final OkHttpClient httpClientStream;
    private final OrderBookStreamHandler orderBookStreamHandler;
    private final String apiKey;
    private final String secret;
    private final JumpTradingThread thread;
    private final List<String> topics;
    private Long lastTime;
    private String lastEvent;
    private Long lastPing;
    private Long lease; // 租约有效期, 超过这个时间将被销毁
    private JumpTradingProxy proxy;

    public JumpTradingFactory(String connid, String streamHost, String apiKey, String secret,
            ConnectType connectType, CombineMessageListener combineMessageListener,
            InetSocketAddress proxy) {
        this.thread = JumpTradingThread.create(this);
        this.headers = Headers.of("connid", connid, "Connection", "close");
        this.streamHost = streamHost;
        this.apiKey = apiKey;
        this.secret = secret;
        this.auth = this.authentication(apiKey, secret);
        this.connectType = connectType;
        this.httpClientStream = HttpClientFactory.newClient(proxy, true);
        this.httpClient = HttpClient.create(null, proxy);
        this.orderBookStreamHandler = new OrderBookStreamHandler(connid, combineMessageListener);
        this.topics = new ArrayList<>();
        this.lastEvent = null;
        this.lastTime = null;
        this.lastPing = null;
        this.lease = null;
        this.proxy = new JumpTradingProxy(this);
    }


    public JSONObject authentication(String apiKey, String secret) {
        return new JSONObject().put("apiKey", apiKey).put("secret", secret);
    }

    /**
     * 获取请求连接地址
     * 
     * @param endpoint 请求地址
     * @param payload 请求参数
     * @return
     */
    public String getConnector(String host, String endpoint, Object payload) {
        StringBuilder builder = new StringBuilder(String.join("/", host.replaceFirst("(/+)$", ""),
                endpoint.replaceFirst("^(/+)", "")));
        String nonce =
                String.valueOf(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli() / 1000);
        try {
            String payloadString = payload == null ? "{}" : HttpAssets.toJsonString(payload);
            String payload64 = Base64.getEncoder()
                    .encodeToString(payloadString.getBytes(StandardCharsets.UTF_8));
            String signature = HmacUtil.encryptStr(this.apiKey + payload64 + nonce, this.secret,
                    HmacUtil.ALGORITHM_SHA384, StandardCharsets.UTF_8.toString());
            builder.append("?APIKEY=").append(this.apiKey).append("&PAYLOAD=").append(payload64)
                    .append("&NONCE=").append(nonce).append("&SIGNATURE=").append(signature);
            return builder.toString();
        } catch (Exception e) {
            throw new RuntimeException("jt构建加密参数异常", e);
        }
    }

    // 发送post请求
    public <T> T post(String memo, String url, Object body, TypeReference<T> typeRef) {
        final String jtConnectorString = this.getConnector(this.streamHost, url, body);
        HttpClientResult httpClientResult = this.httpClient.postJson(jtConnectorString, null, body);
        if (httpClientResult == null) {
            throw new RestException(R.TIME_OUT, "超时");
        }
        if (!httpClientResult.isSuccess()) {
            throw new RestException(R.FAILED, HttpAssets.toJsonString(httpClientResult));
        }
        return HttpAssets.parseJson(httpClientResult.getResult(), typeRef);
    }

    // 建立长连接
    public JumpTradingFactory createOrderBookStream() {
        final String endpoint = "/v2/streamingEvents";
        final String jtConnectorString = this.getConnector(this.streamHost, endpoint, "{}");
        Response response = null;
        BufferedReader in = null;
        Request.Builder builder = new Request.Builder();
        builder.headers(headers);
        Request request = new Request.Builder().url(jtConnectorString).get().build();
        try {
            orderBookStreamHandler.setConnectStatus(ConnectStatus.CONNECTING);
            response = httpClientStream.newCall(request).execute();
            ResponseBody responseBody = response.body();
            if (responseBody == null) {
                orderBookStreamHandler.setConnectStatus(ConnectStatus.FAILED);
                return this;
            }
            in = new BufferedReader(new InputStreamReader(responseBody.byteStream()));
            orderBookStreamHandler.setConnectStatus(ConnectStatus.ONLINE);
            orderBookStreamHandler.onOpenTrigger(request);
            String line;
            while ((line = in.readLine()) != null) {
                orderBookStreamHandler.onStreamMessage(request, line);
            }
        } catch (SocketTimeoutException e) {
            orderBookStreamHandler.setConnectStatus(ConnectStatus.DAMAGED);
            orderBookStreamHandler.onDisconnectTrigger(request, e);
            LOGGER.error("JumpTrading建立OrderBookStream连接超时: url={}, errorMessage={}", endpoint,
                    e.getMessage());
        } catch (IOException e) {
            if (this.orderBookStreamHandler.getConnectStatus().equals(ConnectStatus.DESTROY)) {
                return this;
            }
            orderBookStreamHandler.setConnectStatus(ConnectStatus.DAMAGED);
            orderBookStreamHandler.onDisconnectTrigger(request, e);
            LOGGER.error("JumpTrading建立OrderBookStream连接IO异常: url={}, errorMessage={}", endpoint,
                    e.getMessage());
        } catch (Exception e) {
            if (!this.orderBookStreamHandler.getConnectStatus().equals(ConnectStatus.DESTROY)) {
                LOGGER.error(
                        "JumpTrading建立OrderBookStream连接异常:  connid={}, url={}, status={}, errorMessage={}",
                        this.connid(), endpoint, this.orderBookStreamHandler.getConnectStatus(),
                        e.getMessage());
                orderBookStreamHandler.setConnectStatus(ConnectStatus.DAMAGED);
                orderBookStreamHandler.onDisconnectTrigger(request, e);
            }
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    LOGGER.error("JumpTrading建立OrderBookStream连接IOO异常: url={}, errorMessage={}",
                            endpoint, e.getMessage());
                }
            }
            if (response != null) {
                response.close();
            }
        }
        return this;
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
        json.put("vender", VENDER.JumpTrading);
        json.put("connectType", this.connectType);
        json.put("headers", this.readHeaders());
        json.put("stream-host", this.streamHost);
        json.put("topic", this.topics());
        json.put("lastEvent",
                this.lastEvent == null ? "N/a" : (this.lastEvent + "/" + this.lastTime));
        json.put("lastPing", this.lastPing == null ? "N/a" : (this.lastPing + "/" + this.lease));
        JSONObject authjson = new JSONObject();
        if (this.auth != null && this.auth.has("apiKey")) {
            authjson.put("apiKey",
                    TradiTexAssists.shorterContent(this.auth.getString("apiKey"), (short) 8));
            authjson.put("secret",
                    TradiTexAssists.shorterContent(this.auth.getString("secret"), (short) 8));
        }
        json.put("auth", authjson);
        json.put("status", this.orderBookStreamHandler.getConnectStatus().name());
        return json;
    }

    @Override
    public ConnectStatus status() {
        if (this.orderBookStreamHandler == null) {
            return null;
        }
        return this.orderBookStreamHandler.getConnectStatus();
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
            if (auth.has("secret")) {
                return _otp(connid, auth.getString("secret"));
            }
        }
        return null;
    }

    @Override
    public Boolean otp(String connid, String otp) {
        if (this.config().has("auth")) {
            JSONObject auth = this.config().getJSONObject("auth");
            if (auth.has("secret")) {
                String theOtp = _otp(connid, auth.getString("secret"));
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
        this.httpClientStream.dispatcher().executorService().shutdown();
        this.orderBookStreamHandler.setConnectStatus(ConnectStatus.DESTROY);
        this.thread.shutdown();
        LOGGER.info("JumpTrading-连接已销毁: connid={}, status={}, connectConfig={}", this.connid(),
                this.orderBookStreamHandler.getConnectStatus(), this.config());
    }

    @Override
    public void connect(boolean retryer) {
        if (retryer) {
            LOGGER.info("JumpTrading启动重连: connid={}, connectConfig={}", this.connid(),
                    this.config());
            this.thread.start();
            return;
        }
        LOGGER.info("JumpTrading建立连接: connid={}, connectConfig={}", this.connid(), this.config());
        this.thread.start();
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

    public OrderBookStreamHandler orderBookStreamHandler() {
        return this.orderBookStreamHandler;
    }

    public JumpTradingProxy proxy() {
        return this.proxy;
    }

}
