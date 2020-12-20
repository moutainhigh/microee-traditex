package com.microee.traditex.inbox.app.actions;

import java.util.HashMap;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.core.type.TypeReference;
import com.microee.plugin.http.assets.HttpAssets;
import com.microee.plugin.http.assets.HttpClientResult;
import com.microee.plugin.response.R;
import com.microee.traditex.inbox.app.components.TradiTexConnectComponent;
import com.microee.traditex.inbox.app.validator.RestValidator;
import com.microee.traditex.inbox.oem.connector.ITridexTradFactory;
import com.microee.traditex.inbox.oem.connector.TradiTexConnection;
import com.microee.traditex.inbox.oem.rmi.ITradiTexOandaRMi;
import com.microee.traditex.inbox.up.oanda.OandaTradFactory;

// oanda 外汇相关
@RestController
@RequestMapping("/traditex-oanda")
public class TradiTexOandaRestful implements ITradiTexOandaRMi {

    @Autowired
    private TradiTexConnectComponent connectionComponent;
    
    @Autowired
    private RestValidator restValidator;
    
    // http://developer.oanda.com/rest-live-v20/pricing-ep/
    // #### oanda 外汇
    // 启动 oanda stream 推送
    @RequestMapping(value = "/pricing/query", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<JSONObject> oandaPricingQuery(
            @RequestParam("connid") String connid, 
            @RequestParam("resthost") String resthost, 
            @RequestParam("instruments") String[] instruments) {
        Assertions.assertThat(connid).withFailMessage("%s 必传", "connid").isNotBlank();
        Assertions.assertThat(resthost).withFailMessage("%s 必传", "resthost").isNotBlank();
        Assertions.assertThat(instruments).withFailMessage("%s 非空", "instruments").isNotEmpty();
        restValidator.connIdValid(connid);
        TradiTexConnection<?> conn = connectionComponent.get(connid);
        restValidator.connIdFun(connid, conn).connIdIllegalOanda(connid);
        ITridexTradFactory factory = conn.getFactory();
        OandaTradFactory oandaFactory = (OandaTradFactory)factory;
        HttpClientResult httpClientResult = oandaFactory.queryPrice(resthost, instruments);
        if (httpClientResult == null) {
            return R.failed(R.TIME_OUT, "超时");
        }
        return R.ok(new JSONObject(httpClientResult.getResult()));
    }

    // 查询账户币种余额
    @RequestMapping(value = "/balance/query", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<Map<String, Object>> queryAccountBalance(
            @RequestParam("connid") String connid, 
            @RequestParam("resthost") String resthost, 
            @RequestParam("currency") String currency) {
        Assertions.assertThat(currency).withFailMessage("%s 非空", "currency").isNotEmpty();
        R<JSONObject> summaryResult = this.queryAccountSummary(connid, resthost);
        if (!R.isok(summaryResult.getCode())) {
            return R.failed(summaryResult.getCode(), summaryResult.getMessage());
        }
        if (!summaryResult.getData().has("account")) {
            return R.ok(null);
        }
        JSONObject account = summaryResult.getData().getJSONObject("account");
        if (!account.has("currency")) {
            return R.ok(null);
        }
        Map<String, Object> map = new HashMap<>();
        map.put(account.getString("currency"), account.getString("balance"));
        return R.ok(map);
    }

    // 查询账户信息
    @RequestMapping(value = "/account/summary", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<JSONObject> queryAccountSummary(
            @RequestParam("connid") String connid, 
            @RequestParam("resthost") String resthost) {
        Assertions.assertThat(connid).withFailMessage("%s 必传", "connid").isNotBlank();
        Assertions.assertThat(resthost).withFailMessage("%s 必传", "resthost").isNotBlank();
        restValidator.connIdValid(connid);
        TradiTexConnection<?> conn = connectionComponent.get(connid);
        restValidator.connIdFun(connid, conn).connIdIllegalOanda(connid);
        ITridexTradFactory factory = conn.getFactory();
        OandaTradFactory oandaFactory = (OandaTradFactory)factory;
        HttpClientResult httpClientResult = oandaFactory.querySummary(resthost);
        if (httpClientResult == null) {
            return R.failed(R.TIME_OUT, "超时");
        }
        if (!httpClientResult.isSuccess()) {
            return R.failed(R.FAILED, "查询失败");
        }
        return R.ok(new JSONObject(httpClientResult.getResult()));
    }

    // 创建订单
    @RequestMapping(value = "/order/create", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<Map<String, Object>> createOrder(
            @RequestParam("connid") String connid, 
            @RequestParam("resthost") String resthost, 
            @RequestParam("side") String side, // BUY or SELL
            @RequestParam("symbol") String symbol, // USD_JPY
            @RequestParam("units") String units) {
        Assertions.assertThat(connid).withFailMessage("%s 必传", "connid").isNotBlank();
        Assertions.assertThat(resthost).withFailMessage("%s 必传", "resthost").isNotBlank();
        Assertions.assertThat(side).withFailMessage("%s 必传", "side").isNotBlank();
        Assertions.assertThat(symbol).withFailMessage("%s 必传", "symbol").isNotBlank();
        Assertions.assertThat(units).withFailMessage("%s 必传", "units").isNotBlank();
        restValidator.connIdValid(connid);
        TradiTexConnection<?> conn = connectionComponent.get(connid);
        restValidator.connIdFun(connid, conn).connIdIllegalOanda(connid);
        ITridexTradFactory factory = conn.getFactory();
        OandaTradFactory oandaFactory = (OandaTradFactory)factory;
        HttpClientResult httpClientResult = oandaFactory.createOrder(resthost, side, symbol, units);
        if (httpClientResult == null) {
            return R.failed(R.TIME_OUT, "超时");
        }
        if (!httpClientResult.isSuccess()) {
            return R.failed(R.FAILED, "创建订单失败`" + httpClientResult.getMessage() + "`");
        }
        return R.ok(HttpAssets.parseJson(httpClientResult.getResult(), new TypeReference<Map<String, Object>>() {}));
    }
    
}
