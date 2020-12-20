package com.microee.traditex.inbox.up.hbitex;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringJoiner;
import java.util.TreeMap;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.ImmutableList;
import com.microee.plugin.http.assets.HttpAssets;
import com.microee.plugin.http.assets.HttpClient;
import com.microee.plugin.http.assets.HttpClientLogger;
import com.microee.plugin.http.assets.HttpClientResult;
import com.microee.plugin.http.assets.HttpWebsocketListener;
import com.microee.plugin.response.R;
import com.microee.plugin.response.exception.RestException;
import com.microee.traditex.inbox.oem.connector.ITridexTradFactory;
import com.microee.traditex.inbox.oem.constrants.ConnectStatus;
import com.microee.traditex.inbox.oem.hbitex.ApiSignature;
import com.microee.traditex.inbox.oem.hbitex.ConnectType;
import com.microee.traditex.inbox.oem.hbitex.HBiTexAccount;
import com.microee.traditex.inbox.oem.hbitex.HBiTexHttpResult;
import com.microee.traditex.inbox.oem.hbitex.po.HBiTexOrderPlaceParam;
import com.microee.traditex.inbox.oem.hbitex.vo.AccountBalance;
import com.microee.traditex.inbox.oem.hbitex.vo.HBiTexOrderDetails;
import com.microee.traditex.inbox.oem.hbitex.vo.OrderMatchresults;
import com.microee.traditex.inbox.up.CombineMessageListener;
import com.microee.traditex.inbox.up.TradiTexAssists;
import okhttp3.Headers;

public class HBiTexTradFactory implements ITridexTradFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(HBiTexTradFactory.class); 
    
    private final String uid;
    private final String connid;
    private final String wshost;
    private Headers headers;
    private final JSONObject auth;
    private final ConnectType connectType;
    private final HttpClient httpClient;
    private final HttpClient wsHttpClient;
    private final HBiTexOrderBookWebsocketHandler wsOrderBookHandler;
    private final HBiTexOrderBalanceWebsocketHandler wsOrderBalanceHandler;
    private final HBiTexOrderBookThread orderBookThread;
    private final HBiTexOrderBalanceThread orderBalanceThread;
    private final List<String> topics;
    private Long lastTime;
    private String lastEvent;
    private Long lastPing; // 最后一次续约时间
    private Long lease; // 租约有效期, 超过这个时间将被销毁

    public HBiTexTradFactory(String connid, String wshost, String exchangeCode, ConnectType connectType, 
            CombineMessageListener combineMessageListener, InetSocketAddress proxy, HttpClientLogger log) {
        this.uid = null;
        this.connid = connid;
        this.wshost = wshost;
        this.headers = Headers.of("connid", connid, "cloud-exchange", exchangeCode);
        this.auth = new JSONObject();
        this.connectType = connectType;
        this.wsHttpClient = HttpClient.create(null, proxy);
        this.httpClient = HttpClient.create(null, proxy).setListener(log);
        this.wsOrderBookHandler = new HBiTexOrderBookWebsocketHandler(this.connid, combineMessageListener);
        this.wsOrderBalanceHandler = new HBiTexOrderBalanceWebsocketHandler(this.connid, combineMessageListener);
        this.orderBookThread = new HBiTexOrderBookThread(this);
        this.orderBalanceThread = new HBiTexOrderBalanceThread(this);
        this.topics = new ArrayList<>();
        this.lastEvent = null;
        this.lastTime = null;
        this.lastPing = null;
        this.lease = null;
    }
    
    public HBiTexTradFactory(String connid, String uid, String wshost, String exchangeCode, ConnectType connectType, 
            CombineMessageListener combineMessageListener, InetSocketAddress proxy, HttpClientLogger log) {
        this.uid = uid;
        this.connid = connid;
        this.wshost = wshost;
        this.headers = Headers.of("connid", connid, "cloud-exchange", exchangeCode, "uid", uid);
        this.auth = new JSONObject();
        this.connectType = connectType;
        this.wsHttpClient = HttpClient.create(null, proxy);
        this.httpClient = HttpClient.create(null, proxy).setListener(log);
        this.wsOrderBookHandler = new HBiTexOrderBookWebsocketHandler(this.connid, combineMessageListener);
        this.wsOrderBalanceHandler = new HBiTexOrderBalanceWebsocketHandler(this.connid, combineMessageListener);
        this.orderBookThread = new HBiTexOrderBookThread(this);
        this.orderBalanceThread = new HBiTexOrderBalanceThread(this);
        this.topics = new ArrayList<>();
        this.lastEvent = null;
        this.lastTime = null;
        this.lastPing = null;
        this.lease = null;
    }

    @Override
    public Object factory() {
        return this;
    }

    @Override
    public String connid() {
        return this.connid;
    }
    
    public String uid() {
        return this.uid;
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
        return this.topics.remove(topic);
    }
    
    @Override
    public JSONObject config() {
        JSONObject json = new JSONObject();
        json.put("vender", "HBiTex");
        json.put("connectType", this.connectType);
        json.put("headers", ImmutableList.copyOf(this.headers.iterator()).toString()); 
        json.put("wshost", this.wshost);
        json.put("status", this.status() == null ? "N/a" : this.status().name());
        json.put("topic", this.topics());
        json.put("lastEvent", this.lastEvent == null ? "N/a" : (this.lastEvent + "/" + this.lastTime));
        json.put("lastPing", this.lastPing == null ? "N/a" : (this.lastPing + "/" + this.lease));
        if (ConnectType.ORDER_BALANCE.equals(this.connectType)) {
            JSONObject authjson = new JSONObject();
            if (this.auth != null && this.auth.has("accessKey")) {
                authjson.put("accessKey", TradiTexAssists.shorterContent(this.auth.getString("accessKey"), (short)8));
            }
            if (this.auth != null && this.auth.has("secretKey")) {
                authjson.put("secretKey", TradiTexAssists.shorterContent(this.auth.getString("secretKey"), (short)8));
            }
            json.put("auth", authjson);
        }
        if (ConnectType.ORDER_BOOK.equals(this.connectType)) {
            // ..
        }
        return json;
    }

    @Override
    public ConnectStatus status() {
        if (this.connectType.equals(ConnectType.ORDER_BALANCE)) {
            if (this.wsOrderBalanceHandler == null) {
                return ConnectStatus.UNKNOW;
            }
            return this.wsOrderBalanceHandler.getConnectStatus();
        }
        if (this.connectType.equals(ConnectType.ORDER_BOOK)) {
            if (this.wsOrderBookHandler == null) {
                return ConnectStatus.UNKNOW;
            }
            return this.wsOrderBookHandler.getConnectStatus();
        }
        return ConnectStatus.UNKNOW;
    }

    @Override
    public String genOTP(String connid) {
        if (this.config().has("auth")) {
            JSONObject auth = this.config().getJSONObject("auth");
            if (auth.has("accessKey") && auth.has("secretKey")) {
                return _otp(connid, auth.getString("accessKey"), auth.getString("secretKey"));
            }
        }
        return null;
    }
    
    @Override
    public Boolean otp(String connid, String otp) {
        if (this.config().has("auth")) {
            JSONObject auth = this.config().getJSONObject("auth");
            if (auth.has("accessKey") && auth.has("secretKey")) {
                String theOtp = _otp(connid, auth.getString("accessKey"), auth.getString("secretKey"));
                return theOtp.equals(otp);
            }
        }
        return false;
    }
    
    private String _otp(String connid, String accessKey, String secretKey) {
        return DigestUtils.md5Hex(String.format("%s:%s:%s", connid, accessKey, secretKey));
    }
    
    public void addHeaders(Headers theHeaders) {
        if (theHeaders == null) {
            return;
        }
        Map<String, String> currentHeaderMap = new HashMap<>();
        if (this.headers != null) {
            this.headers.forEach(a -> {
                currentHeaderMap.put(a.getFirst(), a.getSecond());
            });
        }
        theHeaders.forEach(a -> {
            currentHeaderMap.put(a.getFirst(), a.getSecond());
        });
        this.headers = Headers.of(currentHeaderMap);
    }

    public HBiTexTradFactory createWebSocketForOrderbook() {
        this.wsHttpClient.websocket(wshost, this.headers, new HttpWebsocketListener(this.wsOrderBookHandler));
        return this;
    }

    public HBiTexTradFactory createWebSocketForOrderBalance() {
        this.wsHttpClient.websocket(wshost, this.headers, new HttpWebsocketListener(this.wsOrderBalanceHandler));
        return this;
    }

    @Override
    public void shutdown() {
        LOGGER.info("HBiTex-关闭连接: connid={}, connectConfig={}", this.connid(), this.config());
        if (this.connectType.equals(ConnectType.ORDER_BALANCE)) {
            this.wsOrderBalanceHandler.closeWebsocket();
            this.orderBalanceThread.shutdown();
        }
        if (this.connectType.equals(ConnectType.ORDER_BOOK)) {
            this.wsOrderBookHandler.closeWebsocket();
            this.orderBookThread.shutdown();
        }
    }
    
    /**
     * 建立 ws 连接
     */
    @Override
    public void connect(boolean retryer) {
        if (retryer) {
            if (this.connectType.equals(ConnectType.ORDER_BALANCE)) {
                this.wsOrderBalanceHandler.setConnectStatus(ConnectStatus.CONNECTING);
                LOGGER.info("HBiTex-orderbalancd-启动重连: connid={}, connectConfig={}", this.connid(), this.config());
                this.orderBalanceThread.start();
            }
            if (this.connectType.equals(ConnectType.ORDER_BOOK)) {
                this.wsOrderBookHandler.setConnectStatus(ConnectStatus.CONNECTING);
                LOGGER.info("HBiTex-orderbook-启动重连: connid={}, connectConfig={}", this.connid(), this.config());
                this.orderBookThread.start();
            }
            return;
        }
        if (this.connectType.equals(ConnectType.ORDER_BALANCE)) {
            this.wsOrderBalanceHandler.setConnectStatus(ConnectStatus.CONNECTING);
            LOGGER.info("HBiTex-orderbalancd-启动重连: connid={}, connectConfig={}", this.connid(), this.config());
            this.orderBalanceThread.start();
        }
        if (this.connectType.equals(ConnectType.ORDER_BOOK)) {
            this.wsOrderBookHandler.setConnectStatus(ConnectStatus.CONNECTING);
            LOGGER.info("HBiTex-orderbook-建立连接: connid={}, connectConfig={}", this.connid(), this.config());
            this.orderBookThread.start();
        }
    }

    private HttpClientResult doGet(String endpoint, Map<String, Object> queryMap) {
        HttpClientResult httpClientResult = null;
        try {
            httpClientResult = this.httpClient.doGetWithQueryParams(endpoint, this.headers, queryMap);
        } catch (Exception e) {
            throw new RestException(R.SERVER_ERROR, e.getMessage());
        }
        if (httpClientResult == null) {
            throw new RestException(R.TIME_OUT, "超时");
        }
        if (!httpClientResult.isSuccess()) {
            throw new RestException(R.FAILED, HttpAssets.toJsonString(httpClientResult));
        }
        return httpClientResult;
    }

    private HttpClientResult doGet(String endpoint, Map<String, Object> queryMap, HBiTexAccount hbiTexAccount) {
        String connector = HBiTexTradFactory.getConnector("get", endpoint, queryMap, hbiTexAccount.getAccesskey(), hbiTexAccount.getSecreckey());
        HttpClientResult httpClientResult = null;
        try {
            httpClientResult = this.httpClient.doGetWithQueryParams(connector, this.headers, null);
        } catch (Exception e) {
            throw new RestException(R.SERVER_ERROR, e.getMessage());
        }
        if (httpClientResult == null) {
            throw new RestException(R.TIME_OUT, "超时");
        }
        if (!httpClientResult.isSuccess()) {
            throw new RestException(R.FAILED, HttpAssets.toJsonString(httpClientResult));
        }
        return httpClientResult;
    }
    
    public HttpClientResult doPost(String endpoint, Object bodyParam, HBiTexAccount hbiTexAccount) {
        String connector = HBiTexTradFactory.getConnector("post", endpoint, null, hbiTexAccount.getAccesskey(), hbiTexAccount.getSecreckey());
        HttpClientResult httpClientResult = null;
        try {
            httpClientResult = this.httpClient.postJsonBody(connector, this.headers, this.httpClient.getJsonString(bodyParam));
        } catch (Exception e) {
            throw new RestException(R.SERVER_ERROR, e.getMessage());
        }
        if (httpClientResult == null) {
            throw new RestException(R.TIME_OUT, "超时");
        }
        if (!httpClientResult.isSuccess()) {
            throw new RestException(R.FAILED, HttpAssets.toJsonString(httpClientResult));
        }
        return httpClientResult;
    }
    
    public static HttpClientResult doPostParam(HttpClient httpClient, Headers headers, String endpoint, Object bodyParam, HBiTexAccount hbiTexAccount) {
        String connector = HBiTexTradFactory.getConnector("post", endpoint, null, hbiTexAccount.getAccesskey(), hbiTexAccount.getSecreckey());
        HttpClientResult httpClientResult = null;
        try {
            httpClientResult = httpClient.postJsonBody(connector, headers, HttpAssets.toJsonString(bodyParam));
        } catch (Exception e) {
            throw new RestException(R.SERVER_ERROR, e.getMessage());
        }
        if (httpClientResult == null) {
            throw new RestException(R.TIME_OUT, "超时");
        }
        if (!httpClientResult.isSuccess()) {
            throw new RestException(R.FAILED, HttpAssets.toJsonString(httpClientResult));
        }
        return httpClientResult;
    }
    
    public static String getConnector(String method, String hostname, Map<String, Object> queryMap, String accessKey, String secrecKey) {
        Map<String, Object> map = new TreeMap<>(new Comparator<String>() {
            @Override
            public int compare(String str1, String str2) {
                return str1.compareTo(str2);
            }
        });
        if (queryMap != null && queryMap.size() > 0) {
            map.putAll(queryMap);
        }
        map.put("AccessKeyId", accessKey);
        map.put("Timestamp", Instant.ofEpochSecond(Instant.now().getEpochSecond()).atZone(ZoneId.of("Z")).format(DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss")));
        map.put("SignatureMethod", "HmacSHA256");
        map.put("SignatureVersion", 2);
        StringJoiner joiner = new StringJoiner("&");
        for (Entry<String, Object> e : map.entrySet()) {
            if (e.getValue() == null) {
                continue;
            }
            joiner.add(e.getKey() + "=" + urlEncode(e.getValue().toString()));

        }
        String queryStringJoin = joiner.toString();
        URI uri = getURI(hostname);
        String payload = String.join("\n", method.toUpperCase(), uri.getHost(), uri.getPath(),queryStringJoin);
        String result = hostname + "?" + queryStringJoin + "&Signature=" + urlEncode(getSignature(payload, secrecKey));
        LOGGER.info("HBiTexConnectorString={}, payload={}", result, payload);
        return result;
    }

    public static String getSignature(String data, String key) {
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            return encoderbase64(sha256_HMAC.doFinal(data.getBytes()));
        } catch (IllegalStateException | NoSuchAlgorithmException | InvalidKeyException e) {
            LOGGER.error("errorMessage={}", e.getMessage());
        }
        return null;
    }

    public static String encoderbase64(byte[] str)  {
        try {
            return new String(new org.apache.commons.codec.binary.Base64().encode(str), StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }
    
    public static URI getURI(String s) {
        try {
            return new URI(s.trim());
        } catch (URISyntaxException e) {
            LOGGER.error("url={}, errorMessage={}", s, e.getMessage(), e);
            return null;
        }
    }
    
    public static String urlEncode(String payload) {
        try {
            return URLEncoder.encode(payload, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("url={}, errorMessage={}", payload, e.getMessage(), e);
            return null;
        }
    }

    public HBiTexAccount getAccount() {
        if (this.auth == null || !this.auth.has("secretKey")) {
            throw new IllegalStateException("未进行认证");
        }
        String secretKey = this.auth.getString("secretKey");
        String accessKey = this.auth.getString("accessKey");
        String exchangeCode = this.headers.get("cloud-exchange");
        String accountId = this.headers.get("accountId");
        return new HBiTexAccount(accountId, accessKey, secretKey, exchangeCode);
    }
    
    // ws鉴权
    public void authentication(String accessKey, String secretKey) {
        this.auth.put("accessKey", accessKey);
        this.auth.put("secretKey", secretKey);
        Map<String, String> map = new HashMap<>();
        ApiSignature as = new ApiSignature();
        try {
            URI uri = new URI(this.wshost);
            int port = uri.getPort();
            String theHost;
            if (port != -1) {
                theHost = uri.getHost() + ":" + uri.getPort();
            } else {
                theHost = uri.getHost();
            }
            as.createSignature(accessKey, secretKey, "GET", theHost, uri.getPath(), map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        map.put(ApiSignature.op, ApiSignature.opValue);
        map.put("cid", System.currentTimeMillis() + "");
        this.wsOrderBalanceHandler.writeMessage(HttpAssets.toJsonString(map));
    }
    
    /**
     * 订阅相关交易对盘口深度
     * 
     * @param tickers
     */
    public void subscribeDepth(String... tickers) {
        for (String ticker : tickers) {
            if (ticker == null || ticker.trim().isEmpty()) {
                continue;
            }
            //@formatter:off
            String message = String.format("{\"id\":\"%s\",\"sub\":\"market.%s.depth.step0\",\"pick\":[\"asks.30\",\"bids.30\"]}", UUID.randomUUID().toString(), ticker);
            //@formatter:on
            this.wsOrderBookHandler.writeMessage(message);
        }
    }
    
    /**
     * 取消订阅
     */
    public void unsub(String step, String... tickers) {
        // {"id":"28a1342d-49a8-4c81-a903-86700f655251","sub":"market.ltcht.depth.step0","pick":["asks.5","bids.5"]}
        // { "unsub": "topic to unsub", "id": "id generate by client" }
        for (String ticker : tickers) {
            if (ticker == null || ticker.trim().isEmpty()) {
                continue;
            }
            JSONObject json = new JSONObject();
            json.put("unsub", String.format("market.%s.depth.%s", ticker, step));
            json.put("id", UUID.randomUUID().toString());
            this.wsOrderBookHandler.writeMessage(json.toString());
        }
    }

    /**
     * 取消订阅资产或订单状态变动
     * @param topic
     */
    public void unsubtopic(String topic) {
        JSONObject json = new JSONObject();
        json.put("op", "unsub");
        json.put("topic", topic);
        json.put("cid", UUID.randomUUID().toString());
        this.wsOrderBalanceHandler.writeMessage(json.toString());
    }
    
    /**
     * 订阅相关交易对盘口深度
     * 
     * @param tickers
     */
    public void subscribeDepth(String step, Integer pick, String... tickers) {
        for (String ticker : tickers) {
            if (ticker == null || ticker.trim().isEmpty()) {
                continue;
            }
            String pickExp = "";
            if (pick != -1) {
                pickExp = String.format(",\"pick\":[\"asks.%s\",\"bids.%s\"]", pick, pick);
            }
            String message = String.format("{\"id\":\"%s\",\"sub\":\"market.%s.depth.%s\"%s}", UUID.randomUUID().toString(), ticker, step, pickExp);
            this.wsOrderBookHandler.writeMessage(message);
        }
    }

    public HBiTexOrderBookWebsocketHandler getWsOrderBookHandler() {
        return wsOrderBookHandler;
    }

    public HBiTexOrderBalanceWebsocketHandler getWsOrderBalanceHandler() {
        return wsOrderBalanceHandler;
    }

    public ConnectType getConnectType() {
        return connectType;
    }

    /**
     * 查询交易对列表
     * @param resthost
     * @param orderId
     * @return
     */
    public HBiTexHttpResult<List<Map<String, Object>>> querySymbols(String resthost) {
        String endpoint = String.format("%s/v1/common/symbols", resthost);
        HttpClientResult httpClientResult = this.doGet(endpoint, null);
        return HttpAssets.parseJson(httpClientResult.getResult(), new TypeReference<HBiTexHttpResult<List<Map<String, Object>>>>() {});
    }

    /**
     * 查询订单详情
     * @param resthost
     * @param orderId
     * @return
     */
    public HBiTexHttpResult<HBiTexOrderDetails> queryOrderDetail(String resthost, String orderId) {
        HBiTexAccount hbiTexAccount = this.getAccount();
        String endpoint = String.format("%s/v1/order/orders/%s", resthost, orderId);
        HttpClientResult httpClientResult = this.doGet(endpoint, null, hbiTexAccount);
        return HttpAssets.parseJson(httpClientResult.getResult(), new TypeReference<HBiTexHttpResult<HBiTexOrderDetails>>() {});
    }
    /**
     * 查询订单详情
     * @param resthost
     * @param orderId
     * @return
     */
    public HBiTexHttpResult<List<HBiTexOrderDetails>> queryOrders(String resthost, String symbol, String states) {
        HBiTexAccount hbiTexAccount = this.getAccount();
        String endpoint = String.format("%s/v1/order/orders", resthost);
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("symbol", symbol);
        queryMap.put("states", states);
        HttpClientResult httpClientResult = this.doGet(endpoint, queryMap, hbiTexAccount);
        return HttpAssets.parseJson(httpClientResult.getResult(), new TypeReference<HBiTexHttpResult<List<HBiTexOrderDetails>>>() {});
    }
    
    /**
     * 根据客户端订单id查询订单详情
     * @param resthost
     * @param clientOrderId
     * @return
     */
    public HBiTexHttpResult<HBiTexOrderDetails> queryOrderByClientId(String resthost,
            String clientOrderId) {
        HBiTexAccount hbiTexAccount = this.getAccount();
        String endpoint = String.format("%s/v1/order/orders/getClientOrder", resthost);
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("clientOrderId", clientOrderId);
        HttpClientResult httpClientResult = this.doGet(endpoint, queryMap, hbiTexAccount);
        return HttpAssets.parseJson(httpClientResult.getResult(), new TypeReference<HBiTexHttpResult<HBiTexOrderDetails>>() {});
    }
    
    /**
     * 创建订单
     * @param clientOrderId
     * @param orderType
     * @param price
     * @param amount
     * @param side
     */
    public HBiTexHttpResult<String> createOrder(String resthost, HBiTexOrderPlaceParam orderPlaceParam) {
        HBiTexAccount hbiTexAccount = this.getAccount();
        String endpoint = String.format("%s/v1/order/orders/place", resthost);
        orderPlaceParam.setAccountId(hbiTexAccount.getAccountSpotId());
        HttpClientResult httpClientResult = this.doPost(endpoint, orderPlaceParam, hbiTexAccount);
        return HttpAssets.parseJson(httpClientResult.getResult(), new TypeReference<HBiTexHttpResult<String>>() {});
    }

    
    /**
     * 创建订单
     * @param clientOrderId
     * @param orderType
     * @param price
     * @param amount
     * @param side
     */
    public static HBiTexHttpResult<String> createOrder(HttpClient httpClient, Headers headers, String resthost, HBiTexAccount hbiTexAccount, HBiTexOrderPlaceParam orderPlaceParam) {
        String endpoint = String.format("%s/v1/order/orders/place", resthost);
        orderPlaceParam.setAccountId(hbiTexAccount.getAccountSpotId());
        HttpClientResult httpClientResult = doPostParam(httpClient, headers, endpoint, orderPlaceParam, hbiTexAccount);
        return HttpAssets.parseJson(httpClientResult.getResult(), new TypeReference<HBiTexHttpResult<String>>() {});
    }

    // symbol btcjpy
    // 订阅订单更新, 相比现有用户订单更新推送主题“orders.$symbol”， 新增主题“orders.$symbol.update”拥有更低的数据延迟以及更准确的消息顺序
    public void subOrderStat(String symbol) {
        String payload = String.format("{\"op\": \"sub\",\"cid\": \"%s\",\"topic\": \"orders.%s.update\"}", UUID.randomUUID().toString(), symbol);
        LOGGER.info("订阅订单状态变化: symbol={}, payload={}", symbol, payload);
        this.wsOrderBalanceHandler.writeMessage(payload);
    }
    
    /**
     * 撤单
     * @param resthost
     * @param orderId
     * @return
     */
    public HBiTexHttpResult<String> revokeOrder(String resthost, String orderId) {
        HBiTexAccount hbiTexAccount = this.getAccount();
        String endpoint = String.format("%s/v1/order/orders/%s/submitcancel", resthost, orderId);
        HttpClientResult httpClientResult = this.doPost(endpoint, null, hbiTexAccount);
        return HttpAssets.parseJson(httpClientResult.getResult(), new TypeReference<HBiTexHttpResult<String>>() {});
    }
    
    /**
     * 批量撤单
     * @param resthost
     * @param orderId
     * @return
     */
    public HBiTexHttpResult<Map<String, Object>> revokeOrder(String resthost, String[] orderId) {
        HBiTexAccount hbiTexAccount = this.getAccount();
        String endpoint = String.format("%s/v1/order/orders/batchcancel", resthost);
        JSONObject param = new JSONObject();
        param.put("order-ids", orderId);
        HttpClientResult httpClientResult = this.doPost(endpoint, param, hbiTexAccount);
        return HttpAssets.parseJson(httpClientResult.getResult(), new TypeReference<HBiTexHttpResult<Map<String, Object>>>() {});
    }

    /**
     * 查询账户信息
     * @param resthost
     * @return
     */
    public HBiTexHttpResult<List<Map<String, Object>>> queryAccounts(String resthost) {
        HBiTexAccount hbiTexAccount = this.getAccount();
        String endpoint = String.format("%s/v1/account/accounts", resthost);
        HttpClientResult httpClientResult = this.doGet(endpoint, null, hbiTexAccount);
        if (httpClientResult == null) {
            return null;
        }
        return HttpAssets.parseJson(httpClientResult.getResult(), new TypeReference<HBiTexHttpResult<List<Map<String, Object>>>>() {});
    }
    
    // 订阅账户资产变动
    public void subAccount() {
        String payload = String.format("{\"op\": \"sub\",\"cid\": \"%s\",\"topic\": \"accounts\",\"model\": \"0\"}", UUID.randomUUID().toString());
        this.wsOrderBalanceHandler.writeMessage(payload);
    }

    /**
     * 查询订单撮合详情
     * @param resthost
     * @param orderId
     * @return
     */
    public HBiTexHttpResult<List<OrderMatchresults>> queryOrderMatchresults(String resthost, String orderId) {
        HBiTexAccount hbiTexAccount = this.getAccount();
        String endpoint =  resthost + "/v1/order/orders/" + orderId + "/matchresults";
        HttpClientResult httpClientResult = this.doGet(endpoint, null, hbiTexAccount);
        return HttpAssets.parseJson(httpClientResult.getResult(), new TypeReference<HBiTexHttpResult<List<OrderMatchresults>>>() {});
    }

    /**
     * 查询账户余额
     * @param resthost
     * @return
     */
    public HBiTexHttpResult<AccountBalance> querySpotAccountBalance(String resthost) {
        HBiTexAccount hbiTexAccount = this.getAccount();
        String endpoint = String.format("%s/v1/account/accounts/%s/balance", resthost, hbiTexAccount.getAccountSpotId());
        HttpClientResult httpClientResult = this.doGet(endpoint, null, hbiTexAccount);
        return HttpAssets.parseJson(httpClientResult.getResult(), new TypeReference<HBiTexHttpResult<AccountBalance>>() {});
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
        this.lease = this.lastPing + livetime; // 延长5分钟
    }

    @Override
    public Long lease() {
        return this.lease;
    }

}
