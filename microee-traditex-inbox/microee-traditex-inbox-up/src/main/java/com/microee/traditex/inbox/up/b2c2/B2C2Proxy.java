package com.microee.traditex.inbox.up.b2c2;

import java.util.List;
import com.fasterxml.jackson.core.type.TypeReference;
import com.microee.plugin.http.assets.HttpAssets;
import com.microee.traditex.inbox.oem.b2c2.apiparam.B2C2NewOrderParam;
import com.microee.traditex.inbox.oem.b2c2.apiresult.B2C2ApiResultForAccountInfo;
import com.microee.traditex.inbox.oem.b2c2.apiresult.B2C2ApiResultForBalance;
import com.microee.traditex.inbox.oem.b2c2.apiresult.B2C2ApiResultForCurrency;
import com.microee.traditex.inbox.oem.b2c2.apiresult.B2C2ApiResultForInstruments;
import com.microee.traditex.inbox.oem.b2c2.apiresult.B2C2ApiResultForNewOrder;
import com.microee.traditex.inbox.oem.b2c2.subscribe.BCSubscribeInstrument;

public class B2C2Proxy {

    private final B2C2Factory factory;

    public B2C2Proxy(B2C2Factory factory) {
        this.factory = factory;
    }

    // 查询可交易币对
    public List<B2C2ApiResultForInstruments> getInstruments() {
        return this.factory.get("b2c2查询可交易币对", "/instruments", null,
                new TypeReference<List<B2C2ApiResultForInstruments>>() {});
    }

    // 查询余额
    public B2C2ApiResultForBalance getBalance() {
        return this.factory.get("b2c2查询余额", "/balance/", null,
                new TypeReference<B2C2ApiResultForBalance>() {});
    }

    // 获取币种
    public B2C2ApiResultForCurrency getCurrency() {
        return this.factory.get("b2c2获取币种", "/currency", null,
                new TypeReference<B2C2ApiResultForCurrency>() {});
    }

    // 查询账户信息
    public B2C2ApiResultForAccountInfo getAccountInfo() {
        return this.factory.get("b2c2获取账户信息", "/account_info", null,
                new TypeReference<B2C2ApiResultForAccountInfo>() {});
    }

    // 查询订单
    public List<B2C2ApiResultForNewOrder> queryOrder(String orderId) {
        return this.factory.get("b2c2查询订单", "/order/" + orderId + "/", null,
                new TypeReference<List<B2C2ApiResultForNewOrder>>() {});
    }

    // 订阅交易对 BTC/JPY > BTCUST.SPOT
    public String subscribe(String baseCurrency, String quoteCurrency, boolean subscribe) {
        String symbol = String.format("%s%s", baseCurrency, quoteCurrency).toUpperCase();
        Float[] levels = BCSubscribeInstrument.getLevelsByCounter(symbol, quoteCurrency);
        BCSubscribeInstrument param = BCSubscribeInstrument.create(symbol, subscribe, levels);
        this.factory.orderBookStreamHandler().sendMessage(HttpAssets.toJsonString(param));
        return param.getInstrument();
    }

    // 创建订单
    public B2C2ApiResultForNewOrder newOrder(B2C2NewOrderParam param) {
        return this.factory.post("b2c2-RFQ", "/order/", param.param(),
                new TypeReference<B2C2ApiResultForNewOrder>() {});
    }

}
