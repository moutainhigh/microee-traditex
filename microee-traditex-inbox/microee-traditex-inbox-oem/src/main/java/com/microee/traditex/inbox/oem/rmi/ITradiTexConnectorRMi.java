package com.microee.traditex.inbox.oem.rmi;

import java.util.Map;
import java.util.Set;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import com.microee.plugin.response.R;

public interface ITradiTexConnectorRMi {

    // ### 提取一个连接
    @RequestMapping(value = "/get", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<Map<String, JSONObject>> get(@RequestParam("connid") String connid);
    
    // #### 生成一个连接ID
    @RequestMapping(value = "/genid", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<String> genid() ;

    // ### 销毁一个连接
    @RequestMapping(value = "/destroy", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<Set<String>> destroy(@RequestParam("connid") String connid,
            @RequestParam("ONE_TIME_PASSWORD") String otp) ;

    // ### 关闭所有连接
    @RequestMapping(value = "/shutdown", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<Set<String>> shutdown(@RequestBody String[] connids);
    
    // #### orderbook
    // 建立 orderbook ws, 返回新建立的 websocket 的唯一ID
    @RequestMapping(value = "/hbitex/orderbook/new", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<String> hbitexOrderBookNew(@RequestParam("connid") String connid,
            @RequestParam("wshost") String wshost,
            @RequestParam("exchangeCode") String exchangeCode,
            @RequestParam(value = "proxy-address", required = false) String proxyAddress,
            @RequestParam(value = "proxy-port", required = false) Integer proxyPost) ;

    // 订阅 orderbook 变动
    @RequestMapping(value = "/hbitex/orderbook/sub", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<Boolean> hbitexOrderBookSub(@RequestParam("connid") String connid,
            @RequestParam("step") String step, @RequestParam("symbol") String symbol) ;

    // 取消订阅 orderbook 变动
    @RequestMapping(value = "/hbitex/orderbook/unsub", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<Boolean> hbitexOrderBookUnsub(@RequestParam("connid") String connid,
            @RequestParam("step") String step, @RequestParam("symbol") String symbol);

    // #### orderbalance
    // 建立 orderbalance ws, 返回新建立的 websocket 的唯一ID
    @RequestMapping(value = "/hbitex/orderbalance/new", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<String> hbitexOrderBalanceNew(@RequestParam("connid") String connid,
            @RequestParam("wshost") String wshost,
            @RequestParam("exchangeCode") String exchangeCode,
            @RequestParam("uid") String uid,
            @RequestParam(value = "proxy-address", required = false) String proxyAddress,
            @RequestParam(value = "proxy-port", required = false) Integer proxyPost) ;

    // #### orderbalance
    // orderbalance 登录成功后, 返回一个一次性口令, 代替后续需要提供 accessKey 的鉴权
    @RequestMapping(value = "/hbitex/orderbalance/login", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<String> hbitexOrderBalanceLogin(@RequestParam("connid") String connid,
            @RequestParam("uid") String uid, 
            @RequestParam(value = "accountId", required=false) String accountId,
            @RequestParam("accessKey") String accessKey,
            @RequestParam("secretKey") String secretKey) ;

    // #### account subscribe 订阅账户资产变动
    @RequestMapping(value = "/hbitex/account/sub", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<Boolean> hbitexAccountSubscribe(@RequestParam("connid") String connid) ;

    // #### order subscribe 订阅订单更新, 相比现有用户订单更新推送主题“orders.$symbol”，
    // 新增主题“orders.$symbol.update”拥有更低的数据延迟以及更准确的消息顺序
    @RequestMapping(value = "/hbitex/order/sub", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<Boolean> hbitexOrderSubscribe(@RequestParam("connid") String connid,
            @RequestParam("symbol") String symbol);

    // #### oanda 外汇
    // 连接 oanda 汇率 stream, 返回新建立的 stream 的唯一ID
    @RequestMapping(value = "/oanda/pricing/stream-setup", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<String> oandaPricingStreamSetup(@RequestParam("stream-host") String streamHost,
            @RequestParam("connid") String connid, @RequestParam("account-id") String accountId,
            @RequestParam("access-token") String accessToken,
            @RequestParam("instruments") String[] instruments,
            @RequestParam(value = "proxy-address", required = false) String proxyAddress,
            @RequestParam(value = "proxy-port", required = false) Integer proxyPost) ;
    
}
