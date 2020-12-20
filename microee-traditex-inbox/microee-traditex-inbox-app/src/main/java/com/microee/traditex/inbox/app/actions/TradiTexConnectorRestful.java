package com.microee.traditex.inbox.app.actions;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.hashids.Hashids;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.microee.plugin.commons.UUIDObject;
import com.microee.plugin.http.assets.HttpClientLogger;
import com.microee.plugin.http.assets.HttpSSLFactory;
import com.microee.plugin.response.R;
import com.microee.traditex.inbox.app.components.CombineMessage;
import com.microee.traditex.inbox.app.components.TradiTexConnectComponent;
import com.microee.traditex.inbox.app.validator.RestValidator;
import com.microee.traditex.inbox.oem.connector.ITridexTradFactory;
import com.microee.traditex.inbox.oem.connector.TradiTexConnection;
import com.microee.traditex.inbox.oem.hbitex.ConnectType;
import com.microee.traditex.inbox.oem.rmi.ITradiTexConnectorRMi;
import com.microee.traditex.inbox.up.b2c2.B2C2Factory;
import com.microee.traditex.inbox.up.cumberland.CumberLandFactory;
import com.microee.traditex.inbox.up.hbitex.HBiTexTradFactory;
import com.microee.traditex.inbox.up.jumptrading.JumpTradingFactory;
import com.microee.traditex.inbox.up.oanda.OandaTradFactory;
import okhttp3.Headers;

/**
 * 管理所有长连接连接
 * 
 * @author zhangchunhui
 */
@RestController
@RequestMapping("/traditex-ws-conns")
public class TradiTexConnectorRestful implements ITradiTexConnectorRMi { 

    private Hashids hashids = null;

    @Autowired
    private TradiTexConnectComponent connectionComponent;

    @Autowired
    private RestValidator restValidator;

    @Autowired
    private HttpClientLogger httpClientLogger; 
    
    @PostConstruct
    public void init() {
        hashids = new Hashids("__connid");
    }

    @Autowired
    private CombineMessage combineMessageComponent;

    // #### 生成一个连接ID
    @RequestMapping(value = "/genid", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<String> genid() {
        Long randomLong = null;
        String connid = null;
        Random ran = new Random();
        do {
            do {
                randomLong = ran.nextLong();
            } while (randomLong > 9007199254740992L);
            connid = hashids.encode(randomLong);
        } while (connid == null || connid.isEmpty() || connectionComponent.hasKey(connid));
        connectionComponent.add(connid);
        return R.ok(connid);
    }

    // ### 查看连接详情
    @RequestMapping(value = "/get", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<Map<String, JSONObject>> get(@RequestParam("connid") String connid) { 
        Map<String, JSONObject> map = connectionComponent.connections();
        if (!map.containsKey(connid)) {
            return R.ok(null);
        }
        Map<String, JSONObject> result = new HashMap<>();
        result.put(connid, map.get(connid));
        return R.ok(result);
    }
    
    // ### 查看所有连接
    @RequestMapping(value = "/conns", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<Map<String, JSONObject>> conns() {
        return R.ok(connectionComponent.connections());
    }

    // ### 关闭所有连接
    @RequestMapping(value = "/shutdown", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<Set<String>> shutdown(@RequestBody String[] connids) {
        return R.ok(connectionComponent.shutdown(connids));
    }
    
    // ### 销毁一个连接
    @RequestMapping(value = "/destroy", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<Set<String>> destroy(@RequestParam("connid") String connid,
            @RequestParam("ONE_TIME_PASSWORD") String otp) {
        Assertions.assertThat(connid).withFailMessage("%s 必传", "connid").isNotBlank();
        Assertions.assertThat(otp).withFailMessage("%s 必传", "ONE_TIME_PASSWORD").isNotBlank();
        return this.shutdown(new String[] { connid }); 
    }

    // ### ping一个连接, 延长该连接的存活时间
    @RequestMapping(value = "/ping", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<Long> ping(@RequestParam("connid") String connid) {
        Assertions.assertThat(connid).withFailMessage("connid is required in query string").isNotBlank();
        Assertions.assertThat(connectionComponent.hasKey(connid)).withFailMessage("connid not exists").isTrue();
        Assertions.assertThat(connectionComponent.exists(connid)).withFailMessage("connid not online").isTrue();
        ITridexTradFactory factory = connectionComponent.factory(connid);
        Assertions.assertThat(factory != null).withFailMessage("connid not connected").isTrue();
        long now = Instant.now().toEpochMilli();
        factory.putLastPing(now, connectionComponent.connectionExpire()); 
        connectionComponent.add(connid, factory);
        return R.ok(factory.lease());
    }

    // ### B2C2 orderbook
    // 建立 b2c2 orderbook ws, 返回新建立的 websocket 的唯一ID
    @RequestMapping(value = "/b2c2/orderbook/new", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<String> b2c2OrderBookNew(@RequestParam("connid") String connid,
            @RequestParam("wsHost") String wsHost,
            @RequestParam("restHost") String restHost,
            @RequestParam("accessToken") String accessToken,
            @RequestParam(value = "proxy-address", required = false) String proxyAddress,
            @RequestParam(value = "proxy-port", required = false) Integer proxyPost) {
        Assertions.assertThat(connid).withFailMessage("%s 必传", "connid").isNotBlank();
        Assertions.assertThat(wsHost).withFailMessage("%s 必传", "wsHost").isNotBlank();
        Assertions.assertThat(restHost).withFailMessage("%s 必传", "restHost").isNotBlank();
        Assertions.assertThat(accessToken).withFailMessage("%s 必传", "accessToken").isNotBlank();
        restValidator.connIdValid(connid);
        InetSocketAddress proxy = proxyAddress == null ? null : new InetSocketAddress(proxyAddress, proxyPost);
        ITridexTradFactory factory = connectionComponent.factory(connid);
        restValidator.validateStaus(connid, factory);
        B2C2Factory jumpTradingFactory = new B2C2Factory(connid, wsHost, restHost, accessToken, ConnectType.ORDER_BOOK, combineMessageComponent, proxy);
        connectionComponent.add(connid, jumpTradingFactory);
        jumpTradingFactory.connect(false);
        return R.ok(connid);
    }

    // ### JumpTrading orderbook
    // 建立 JumpTrading orderbook ws, 返回新建立的 websocket 的唯一ID
    @RequestMapping(value = "/jumptrading/orderbook/new", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<String> jumpTradingOrderBookNew(@RequestParam("connid") String connid,
            @RequestParam("streamHost") String streamHost,
            @RequestParam("apiKey") String apiKey,
            @RequestParam("secret") String secret,
            @RequestParam(value = "proxy-address", required = false) String proxyAddress,
            @RequestParam(value = "proxy-port", required = false) Integer proxyPost) {
        Assertions.assertThat(connid).withFailMessage("%s 必传", "connid").isNotBlank();
        Assertions.assertThat(streamHost).withFailMessage("%s 必传", "streamHost").isNotBlank();
        Assertions.assertThat(apiKey).withFailMessage("%s 必传", "apiKey").isNotBlank();
        Assertions.assertThat(secret).withFailMessage("%s 必传", "secret").isNotBlank();
        restValidator.connIdValid(connid);
        InetSocketAddress proxy = proxyAddress == null ? null : new InetSocketAddress(proxyAddress, proxyPost);
        ITridexTradFactory factory = connectionComponent.factory(connid);
        restValidator.validateStaus(connid, factory);
        JumpTradingFactory jumpTradingFactory = new JumpTradingFactory(connid, streamHost, apiKey, secret, ConnectType.ORDER_BOOK, combineMessageComponent, proxy);
        connectionComponent.add(connid, jumpTradingFactory);
        jumpTradingFactory.connect(false);
        return R.ok(connid);
    }

    // ### CumberLand orderbook
    // 建立 CumberLand orderbook ws, 返回新建立的 websocket 的唯一ID
    @RequestMapping(value = "/cumberland/orderbook/new", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<String> cumberlandOrderBookNew(@RequestParam("connid") String connid,
            @RequestParam("cbHost") String cbHost,
            @RequestParam("counterPartyId") String counterPartyId,
            @RequestParam("userId") String userId,
            @RequestParam("sslfile") MultipartFile sslfile, 
            @RequestParam("sslPassword") String sslPassword,
            @RequestParam(value = "proxy-address", required = false) String proxyAddress,
            @RequestParam(value = "proxy-port", required = false) Integer proxyPost) throws IOException {
        Assertions.assertThat(connid).withFailMessage("%s 必传", "connid").isNotBlank();
        Assertions.assertThat(cbHost).withFailMessage("%s 必传", "cbHost").isNotBlank();
        Assertions.assertThat(counterPartyId).withFailMessage("%s 必传", "counterPartyId").isNotBlank();
        Assertions.assertThat(userId).withFailMessage("%s 必传", "userId").isNotBlank();
        Assertions.assertThat(sslfile).withFailMessage("%s 必传", "sslfile").isNotNull();
        Assertions.assertThat(sslPassword).withFailMessage("%s 必传", "sslPassword").isNotBlank();
        restValidator.connIdValid(connid);
        InetSocketAddress proxy = proxyAddress == null ? null : new InetSocketAddress(proxyAddress, proxyPost);
        ITridexTradFactory factory = connectionComponent.factory(connid);
        restValidator.validateStaus(connid, factory);
        File tempFile = File.createTempFile("ssl-file-" + UUIDObject.get().toString(), ".p12");
        sslfile.transferTo(tempFile);
        CumberLandFactory cumberLandFactory = new CumberLandFactory(connid, cbHost, counterPartyId, userId,
                ConnectType.ORDER_BOOK, combineMessageComponent, proxy, new HttpSSLFactory(tempFile, sslPassword)); 
        connectionComponent.add(connid, cumberLandFactory);
        cumberLandFactory.connect(false);
        if (tempFile.exists()) {
            tempFile.deleteOnExit();
        }
        return R.ok(connid);
    }

    // ### HBiTexTrade orderbook
    // 建立 HBiTexTrade orderbook ws, 返回新建立的 websocket 的唯一ID
    @RequestMapping(value = "/hbitex/orderbook/new", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<String> hbitexOrderBookNew(@RequestParam("connid") String connid,
            @RequestParam("wshost") String wshost,
            @RequestParam("exchangeCode") String exchangeCode,
            @RequestParam(value = "proxy-address", required = false) String proxyAddress,
            @RequestParam(value = "proxy-port", required = false) Integer proxyPost) {
        Assertions.assertThat(wshost).withFailMessage("%s 必传", "wshost").isNotBlank();
        Assertions.assertThat(exchangeCode).withFailMessage("%s 必传", "exchangeCode").isNotBlank();
        restValidator.connIdValid(connid);
        InetSocketAddress proxy =
                proxyAddress == null ? null : new InetSocketAddress(proxyAddress, proxyPost);
        ITridexTradFactory factory = connectionComponent.factory(connid);
        restValidator.validateStaus(connid, factory);
        HBiTexTradFactory hbiTexTradFactory = new HBiTexTradFactory(connid, wshost, exchangeCode, ConnectType.ORDER_BOOK, combineMessageComponent, proxy, httpClientLogger);
        connectionComponent.add(connid, hbiTexTradFactory);
        hbiTexTradFactory.connect(false);
        return R.ok(connid);
    }

    // ### 订阅 HBiTexTrade orderbook 变动
    @RequestMapping(value = "/hbitex/orderbook/sub", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<Boolean> hbitexOrderBookSub(@RequestParam("connid") String connid,
            @RequestParam("step") String step, @RequestParam("symbol") String symbol) {
        Assertions.assertThat(step).withFailMessage("%s 必传", "step").isNotBlank();
        Assertions.assertThat(symbol).withFailMessage("%s 必传", "symbol").isNotBlank();
        TradiTexConnection<?> conn = connectionComponent.get(connid);
        restValidator.connIdValid(connid).connIdFun(connid, conn).connIdIllegalHbiTex(connid);
        HBiTexTradFactory hbiTexTradFactory = (HBiTexTradFactory) conn.getFactory();
        hbiTexTradFactory.subscribeDepth(step, -1, symbol);
        return R.ok(true);
    }

    // ### 取消订阅 HBiTexTrade orderbook 变动
    @RequestMapping(value = "/hbitex/orderbook/unsub", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<Boolean> hbitexOrderBookUnsub(@RequestParam("connid") String connid,
            @RequestParam("step") String step, @RequestParam("symbol") String symbol) {
        Assertions.assertThat(step).withFailMessage("%s 必传", "step").isNotBlank();
        Assertions.assertThat(symbol).withFailMessage("%s 必传", "symbol").isNotBlank();
        TradiTexConnection<?> conn = connectionComponent.get(connid);
        restValidator.connIdValid(connid).connIdFun(connid, conn).connIdIllegalHbiTex(connid);
        HBiTexTradFactory hbiTexTradFactory = (HBiTexTradFactory) conn.getFactory();
        hbiTexTradFactory.unsub(step, symbol);
        return R.ok(true);
    }

    // ### 取消订阅资产或订单状态变动
    @RequestMapping(value = "/hbitex/orderbalance/unsub", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<Boolean> hbitexOrderBalanceUnsub(@RequestParam("connid") String connid,
            @RequestParam("topic") String topic) {
        restValidator.connIdValid(connid);
        Assertions.assertThat(topic).withFailMessage("%s 必传", "topic").isNotBlank();
        TradiTexConnection<?> conn = connectionComponent.get(connid);
        restValidator.connIdValid(connid).connIdFun(connid, conn).connIdIllegalHbiTex(connid);
        HBiTexTradFactory hbiTexTradFactory = (HBiTexTradFactory) conn.getFactory();
        hbiTexTradFactory.unsubtopic(topic);
        return R.ok(true);
    }
    
    // ### HBiTexTrade orderbalance
    // 建立 HBiTexTrade orderbalance ws, 返回新建立的 websocket 的唯一ID
    @RequestMapping(value = "/hbitex/orderbalance/new", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<String> hbitexOrderBalanceNew(@RequestParam("connid") String connid,
            @RequestParam("wshost") String wshost,
            @RequestParam("exchangeCode") String exchangeCode,
            @RequestParam("uid") String uid,
            @RequestParam(value = "proxy-address", required = false) String proxyAddress,
            @RequestParam(value = "proxy-port", required = false) Integer proxyPost) {
        Assertions.assertThat(wshost).withFailMessage("%s 必传", "wshost").isNotBlank();
        Assertions.assertThat(exchangeCode).withFailMessage("%s 必传", "exchangeCode").isNotBlank();
        Assertions.assertThat(uid).withFailMessage("%s 必传", "uid").isNotBlank();
        restValidator.connIdValid(connid);
        ITridexTradFactory factory = connectionComponent.factory(connid);
        restValidator.validateStaus(connid, factory);
        InetSocketAddress proxy =
                proxyAddress == null ? null : new InetSocketAddress(proxyAddress, proxyPost);
        HBiTexTradFactory hbiTexTradFactory = new HBiTexTradFactory(connid, uid, wshost, exchangeCode,
                ConnectType.ORDER_BALANCE, combineMessageComponent, proxy, httpClientLogger);
        connectionComponent.add(connid, hbiTexTradFactory).connect(false);
        return R.ok(connid);
    }

    // ### HBiTexTrade orderbalance
    // orderbalance 登录成功后, 返回一个一次性口令, 代替后续需要提供 accessKey 的鉴权
    @RequestMapping(value = "/hbitex/orderbalance/login", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<String> hbitexOrderBalanceLogin(@RequestParam("connid") String connid,
            @RequestParam("uid") String uid, 
            @RequestParam(value = "accountId", required=false) String accountId,
            @RequestParam("accessKey") String accessKey,
            @RequestParam("secretKey") String secretKey) {
        Assertions.assertThat(uid).withFailMessage("%s 必传", "uid").isNotBlank();
        //Assertions.assertThat(accountId).withFailMessage("%s 必传", "accountId").isNotBlank();
        Assertions.assertThat(accessKey).withFailMessage("%s 必传", "accessKey").isNotBlank();
        Assertions.assertThat(secretKey).withFailMessage("%s 必传", "secretKey").isNotBlank();
        restValidator.connIdValid(connid);
        TradiTexConnection<?> conn = connectionComponent.get(connid);
        restValidator.connIdFun(connid, conn).connIdIllegalHbiTex(connid);
        HBiTexTradFactory hbiTexTradFactory = (HBiTexTradFactory) conn.getFactory();
        if (StringUtils.isEmpty(accountId)) {
            hbiTexTradFactory.addHeaders(Headers.of("uid", uid));
        } else {
            hbiTexTradFactory.addHeaders(Headers.of("uid", uid, "accountId", accountId));
        }
        hbiTexTradFactory.authentication(accessKey, secretKey);
        return R.ok(conn.getFactory().genOTP(connid))
                .message("Please favorite, ONE_TIME_PASSWORD generated.");
    }

    // ### HBiTexTrade account subscribe 订阅账户资产变动
    @RequestMapping(value = "/hbitex/account/sub", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<Boolean> hbitexAccountSubscribe(@RequestParam("connid") String connid) {
        restValidator.connIdValid(connid);
        TradiTexConnection<?> conn = connectionComponent.get(connid);
        restValidator.connIdFun(connid, conn).connIdIllegalHbiTex(connid);
        HBiTexTradFactory hbiTexTradFactory = (HBiTexTradFactory) conn.getFactory();
        hbiTexTradFactory.subAccount();
        return R.ok(true);
    }

    // ### HBiTexTrade order subscribe 订阅订单更新, 相比现有用户订单更新推送主题“orders.$symbol”，
    // 新增主题“orders.$symbol.update”拥有更低的数据延迟以及更准确的消息顺序
    @RequestMapping(value = "/hbitex/order/sub", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<Boolean> hbitexOrderSubscribe(@RequestParam("connid") String connid,
            @RequestParam("symbol") String symbol) {
        Assertions.assertThat(symbol).withFailMessage("%s 必传", "uid").isNotBlank();
        restValidator.connIdValid(connid);
        TradiTexConnection<?> conn = connectionComponent.get(connid);
        restValidator.connIdFun(connid, conn).connIdIllegalHbiTex(connid);
        HBiTexTradFactory hbiTexTradFactory = (HBiTexTradFactory) conn.getFactory();
        hbiTexTradFactory.subOrderStat(symbol);
        return R.ok(true);
    }

    // ### oanda 外汇
    // 连接 oanda 汇率 stream, 返回新建立的 stream 的唯一ID
    @RequestMapping(value = "/oanda/pricing/stream-setup", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<String> oandaPricingStreamSetup(@RequestParam("stream-host") String streamHost,
            @RequestParam("connid") String connid, @RequestParam("account-id") String accountId,
            @RequestParam("access-token") String accessToken,
            @RequestParam("instruments") String[] instruments,
            @RequestParam(value = "proxy-address", required = false) String proxyAddress,
            @RequestParam(value = "proxy-port", required = false) Integer proxyPost) {
        Assertions.assertThat(streamHost).withFailMessage("%s 必传", "stream-host").isNotBlank();
        Assertions.assertThat(connid).withFailMessage("%s 必传", "connid").isNotBlank();
        Assertions.assertThat(accountId).withFailMessage("%s 必传", "account-id").isNotBlank();
        Assertions.assertThat(accessToken).withFailMessage("%s 必传", "access-token").isNotBlank();
        Assertions.assertThat(instruments).withFailMessage("%s 必传", "instruments").isNotEmpty();
        restValidator.connIdValid(connid);
        InetSocketAddress proxy =
                proxyAddress == null ? null : new InetSocketAddress(proxyAddress, proxyPost);
        ITridexTradFactory factory = connectionComponent.factory(connid);
        if (factory != null) {
            return new R<>(R.FUN, connid, "OK");
        }
        OandaTradFactory oandaTradFactory = new OandaTradFactory(instruments, streamHost, connid,
                accountId, accessToken, ConnectType.OANDA_PRICE, combineMessageComponent, proxy);
        connectionComponent.add(connid, oandaTradFactory);
        oandaTradFactory.connect(false);
        return R.ok(connid);
    }

}
