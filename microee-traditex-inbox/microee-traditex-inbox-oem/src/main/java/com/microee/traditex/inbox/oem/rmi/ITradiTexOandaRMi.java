package com.microee.traditex.inbox.oem.rmi;

import java.util.Map;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import com.microee.plugin.response.R;

public interface ITradiTexOandaRMi {


    // http://developer.oanda.com/rest-live-v20/pricing-ep/
    // #### oanda 外汇
    // 启动 oanda stream 推送
    @RequestMapping(value = "/pricing/query", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<JSONObject> oandaPricingQuery(
            @RequestParam("connid") String connid, 
            @RequestParam("resthost") String resthost, 
            @RequestParam("instruments") String[] instruments);

    // 查询账户币种余额
    @RequestMapping(value = "/balance/query", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<Map<String, Object>> queryAccountBalance(
            @RequestParam("connid") String connid, 
            @RequestParam("resthost") String resthost, 
            @RequestParam("currency") String currency);

    // 查询账户信息
    @RequestMapping(value = "/account/summary", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<JSONObject> queryAccountSummary(
            @RequestParam("connid") String connid, 
            @RequestParam("resthost") String resthost);

    // 创建订单
    @RequestMapping(value = "/order/create", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<Map<String, Object>> createOrder(
            @RequestParam("connid") String connid, 
            @RequestParam("resthost") String resthost, 
            @RequestParam("side") String side, // BUY or SELL
            @RequestParam("symbol") String symbol, // USD_JPY
            @RequestParam("units") String units);
    
}
