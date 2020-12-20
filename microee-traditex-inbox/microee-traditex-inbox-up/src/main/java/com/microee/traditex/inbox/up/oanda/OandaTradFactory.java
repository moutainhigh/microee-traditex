package com.microee.traditex.inbox.up.oanda;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.collect.ImmutableList;
import com.microee.plugin.http.assets.HttpAssets;
import com.microee.plugin.http.assets.HttpClient;
import com.microee.plugin.http.assets.HttpClientFactory;
import com.microee.plugin.http.assets.HttpClientResult;
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

public class OandaTradFactory implements ITridexTradFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(OandaTradFactory.class);

    private String streamHost;
    private Headers headers;
    private final JSONObject auth;
    private final ConnectType connectType;
    private final HttpClient httpClient;
    private final OkHttpClient httpClientStream;
    private final OandaStreamHandler oandaStreamHandler;
    private final String accountId;
    private final OandaStreamThread thread;
    private final String[] instruments;
    private final List<String> topics;
    private Long lastTime;
    private String lastEvent;
    private Long lastPing;
    private Long lease; // 租约有效期, 超过这个时间将被销毁

    public OandaTradFactory(String[] instruments, String streamHost, String connid, String accountId, String accessToken,
            ConnectType connectType, CombineMessageListener combineMessageListener,
            InetSocketAddress proxy) {
        this.instruments = instruments;
        this.thread = OandaStreamThread.create(this);
        this.headers = Headers.of("connid", connid, "Authorization",
                String.format("Bearer %s", accessToken), "Accept-Datetime-Format", "UNIX", "Connection", "close");
        this.streamHost = streamHost;
        this.accountId = accountId;
        this.auth = this.authentication(accountId, accessToken);
        this.connectType = connectType;
        this.httpClientStream = HttpClientFactory.newClient(proxy, true);
        this.httpClient = HttpClient.create(null, proxy);
        this.oandaStreamHandler = new OandaStreamHandler(connid, combineMessageListener);
        this.topics = new ArrayList<>();
        this.lastEvent = null;
        this.lastTime = null;
        this.lastPing = null;
        this.lease = null;
    }

    public JSONObject authentication(String accountId, String accessToken) {
        return new JSONObject().put("accessToken", accessToken).put("accountId", accountId);
    }

    public OandaTradFactory createPricingStream() {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("instruments", this.instruments[0]);
        String endpoint =
                String.format("%s/v3/accounts/%s/pricing/stream", streamHost, this.accountId);
        Response response = null;
        BufferedReader in = null;
        Request.Builder builder = new Request.Builder();
        builder.headers(headers);
        String url = HttpAssets.getUrl(endpoint, queryParams);
        Request request = builder.url(url).get().build();
        try {
            oandaStreamHandler.setConnectStatus(ConnectStatus.CONNECTING);
            response = httpClientStream.newCall(request).execute();
            ResponseBody responseBody = response.body();
            if (responseBody == null) {
                oandaStreamHandler.setConnectStatus(ConnectStatus.FAILED);
                return this;
            }
            in = new BufferedReader(new InputStreamReader(responseBody.byteStream()));
            oandaStreamHandler.setConnectStatus(ConnectStatus.ONLINE);
            oandaStreamHandler.onOpenTrigger(request);
            String line;
            while ((line = in.readLine()) != null) {
                oandaStreamHandler.onStreamMessage(request, line);
            }
        } catch (SocketTimeoutException e) {
            oandaStreamHandler.setConnectStatus(ConnectStatus.DAMAGED);
            oandaStreamHandler.onDisconnectTrigger(request, e);
            LOGGER.error("Oanda建立PriceStream连接超时: url={}, errorMessage={}", url, e.getMessage());
        } catch (IOException e) {
            if (this.oandaStreamHandler.getConnectStatus().equals(ConnectStatus.DESTROY)) {
                return this;
            }
            oandaStreamHandler.setConnectStatus(ConnectStatus.DAMAGED);
            oandaStreamHandler.onDisconnectTrigger(request, e);
            LOGGER.error("Oanda建立PriceStream连接IO异常: url={}, errorMessage={}", url, e.getMessage());
        } catch (Exception e) {
            if (!this.oandaStreamHandler.getConnectStatus().equals(ConnectStatus.DESTROY)) {
                LOGGER.error("Oanda建立PriceStream连接异常:  connid={}, url={}, status={}, errorMessage={}", 
                        this.connid(), url, this.oandaStreamHandler.getConnectStatus(), e.getMessage());
                oandaStreamHandler.setConnectStatus(ConnectStatus.DAMAGED);
                oandaStreamHandler.onDisconnectTrigger(request, e);
            }
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    LOGGER.error("Oanda建立PriceStream连接IOO异常: url={}, errorMessage={}", endpoint,
                            e.getMessage());
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

    public String getAccountId() {
        return accountId;
    }

    @Override
    public JSONObject config() {
        JSONObject json = new JSONObject();
        json.put("vender", VENDER.Oanda);
        json.put("connectType", this.connectType);
        json.put("headers", this.readHeaders());
        json.put("stream-host", this.streamHost);
        json.put("topic", this.topics());
        json.put("lastEvent", this.lastEvent == null ? "N/a" : (this.lastEvent + "/" + this.lastTime));
        json.put("lastPing", this.lastPing == null ? "N/a" : (this.lastPing + "/" + this.lease));
        JSONObject authjson = new JSONObject();
        if (this.auth != null && this.auth.has("accessToken")) {
            authjson.put("accessToken",
                    TradiTexAssists.shorterContent(this.auth.getString("accessToken"), (short) 8));
            authjson.put("accountId",
                    TradiTexAssists.shorterContent(this.auth.getString("accountId"), (short) 8));
        }
        json.put("auth", authjson);
        json.put("status", this.oandaStreamHandler.getConnectStatus().name());
        return json;
    }

    @Override
    public ConnectStatus status() {
        if (this.oandaStreamHandler == null) {
            return null;
        }
        return this.oandaStreamHandler.getConnectStatus();
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

    private String _otp(String connid, String accessToken) {
        return DigestUtils.md5Hex(String.format("%s:%s", connid, accessToken));
    }

    @Override
    public void shutdown() {
        this.httpClientStream.dispatcher().executorService().shutdown();
        this.oandaStreamHandler.setConnectStatus(ConnectStatus.DESTROY);
        this.thread.shutdown();
        LOGGER.info("Oanda-连接已销毁: connid={}, status={}, connectConfig={}", this.connid(), this.oandaStreamHandler.getConnectStatus(), this.config());
    }
    
    @Override
    public void connect(boolean retryer) {
        if (retryer) {
            LOGGER.info("Oanda 启动重连: connid={}, connectConfig={}", this.connid(), this.config());
            this.thread.start();
            return;
        }
        LOGGER.info("Oanda 建立连接: connid={}, connectConfig={}", this.connid(), this.config());
        this.thread.start();
    }

    // 查询外汇价格
    public HttpClientResult queryPrice(String resthost, String... symbols) {
        String endpoint = String.format("%s/v3/accounts/%s/pricing", resthost, this.accountId);
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("instruments", symbols[0]);
        return httpClient.doGetWithQueryParams(endpoint, this.headers, queryParams);
    }

    //@formatter:off
    // {
    //  "lastTransactionID": "722701",
    //  "account": {
    //    "createdByUserID": 16061922,
    //    "NAV": "8600.5445",
    //    "marginCloseoutUnrealizedPL": "57.2460",
    //    "openPositionCount": 1,
    //    "withdrawalLimit": "2812.2651",
    //    "positionValue": "144706.9840",
    //    "marginRate": "0.04",
    //    "balance": "8561.9145",
    //    "lastTransactionID": "722701",
    //    "resettablePL": "-2971277.5210",
    //    "financing": "-20160.5645",
    //    "createdTime": "1597125088.721704373",
    //    "alias": "Primary",
    //    "currency": "JPY",
    //    "commission": "0.0000",
    //    "marginCloseoutPercent": "0.67156",
    //    "id": "101-009-16061922-001",
    //    "openTradeCount": 2,
    //    "pendingOrderCount": 0,
    //    "hedgingEnabled": false,
    //    "resettablePLTime": "0",
    //    "marginAvailable": "2812.2651",
    //    "dividendAdjustment": "0",
    //    "marginCloseoutPositionValue": "144706.9840",
    //    "marginCloseoutMarginUsed": "5788.2794",
    //    "unrealizedPL": "38.6300",
    //    "marginCloseoutNAV": "8619.1605",
    //    "guaranteedStopLossOrderMode": "DISABLED",
    //    "marginUsed": "5788.2794",
    //    "guaranteedExecutionFees": "0.0000",
    //    "pl": "-2971277.5210"
    //  }
    // }
    //@formatter:on
    // 查询账户余额
    public HttpClientResult querySummary(String resthost) {
        String endpoint = String.format("%s/v3/accounts/%s/summary", resthost, this.accountId);
        return httpClient.doGetWithQueryParams(endpoint, headers, null);
    }

    //@formatter:off
    // {
    //     "orderCreateTransaction": {
    //         "reason": "CLIENT_ORDER",
    //         "accountID": "101-009-16061922-001",
    //         "requestID": "78768155248967792",
    //         "instrument": "USD_JPY",
    //         "units": "100",
    //         "id": "722702",
    //         "time": "1599922468.993867908",
    //         "type": "MARKET_ORDER",
    //         "batchID": "722702",
    //         "timeInForce": "FOK",
    //         "positionFill": "DEFAULT",
    //         "userID": 16061922
    //     },
    //     "orderCancelTransaction": {
    //         "reason": "MARKET_HALTED",
    //         "accountID": "101-009-16061922-001",
    //         "orderID": "722702",
    //         "requestID": "78768155248967792",
    //         "id": "722703",
    //         "time": "1599922468.993867908",
    //         "type": "ORDER_CANCEL",
    //         "batchID": "722702",
    //         "userID": 16061922
    //     },
    //     "lastTransactionID": "722703",
    //     "relatedTransactionIDs": [
    //         "722702",
    //         "722703"
    //     ]
    // }
    //@formatter:on
    /**
     * 创建订单
     * @param resthost 
     * @param side BUY or SELL
     * @param symbol USD_JPY
     * @param units
     * @return
     */
    public HttpClientResult createOrder(String resthost, 
            String side, 
            String symbol, 
            String units) {
        String unitsString = side.equalsIgnoreCase("buy") ? units : "-" + units; // 买入正数, 卖出负数
        //@formatter:off
        String param = String.format(
                "{ \"order\": { \"units\": \"%s\", \"instrument\": \"%s\", \"timeInForce\": \"FOK\", \"type\": \"MARKET\", \"positionFill\": \"DEFAULT\" } }", unitsString, symbol);
        //@formatter:on
        String endpoint = String.format("%s/v3/accounts/%s/orders", resthost, this.accountId);
        return httpClient.postJsonBody(endpoint, headers, param);
    }

    @Override
    public List<String> topics() {
        return this.topics;
    }

    @Override
    public void addTopic(String topic) {

    }


    @Override
    public boolean removeTopics(String topic) {
        return false;
    }

    public OandaStreamHandler getOandaStreamHandler() {
        return oandaStreamHandler;
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

}
