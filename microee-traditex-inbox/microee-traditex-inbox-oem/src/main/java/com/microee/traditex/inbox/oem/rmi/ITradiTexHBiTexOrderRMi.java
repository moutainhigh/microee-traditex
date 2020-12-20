package com.microee.traditex.inbox.oem.rmi;

import java.util.List;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import com.microee.plugin.response.R;
import com.microee.traditex.inbox.oem.hbitex.HBiTexHttpResult;
import com.microee.traditex.inbox.oem.hbitex.vo.HBiTexOrderDetails;
import com.microee.traditex.inbox.oem.hbitex.vo.OrderMatchresults;

public interface ITradiTexHBiTexOrderRMi {

    // #### symbols
    // 查询交易对
    @RequestMapping(value = "/symbols", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<List<String>> symbols(@RequestParam("connid") String connid,
            @RequestParam("resthost") String resthost);

    // #### symbols
    // 查询交易对
    @RequestMapping(value = "/symbol/get", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<Map<String, Object>> symbol(@RequestParam("connid") String connid,
            @RequestParam("resthost") String resthost, @RequestParam("symbol") String symbol);

    // #### order
    // # 时间同步
    // $ sudo ntpdate time.ntp.org
    // $ date
    // 创建订单
    @RequestMapping(value = "/create", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<HBiTexHttpResult<String>> create(@RequestParam("connid") String connid,
            @RequestParam("client-order-id") String clientOrderId,
            @RequestParam("order-type") String orderType, @RequestParam("symbol") String symbol,
            @RequestParam("price") String price, @RequestParam("amount") String amount,
            @RequestParam("side") String side, // BUY or SELL
            @RequestParam("resthost") String resthost);

    // #### order
    // 查询订单详情
    @RequestMapping(value = "/details", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<HBiTexOrderDetails> details(@RequestParam("connid") String connid,
            @RequestParam("resthost") String resthost, @RequestParam("order-id") String orderId);

    // #### order
    // 根据客户端订单id查询订单详情
    @RequestMapping(value = "/queryOrder", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<HBiTexOrderDetails> queryOrder(@RequestParam("connid") String connid,
            @RequestParam("resthost") String resthost,
            @RequestParam("client-order-id") String clientOrderId);

    // #### order
    // 查询订单撮合详情
    @RequestMapping(value = "/matchs", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<List<OrderMatchresults>> matchs(@RequestParam("connid") String connid,
            @RequestParam("resthost") String resthost, @RequestParam("order-id") String orderId);

    // #### order
    // 撤单
    @RequestMapping(value = "/revoke", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<?> revoke(@RequestParam("connid") String connid,
            @RequestParam("resthost") String resthost, @RequestParam("order-ids") String[] orderId);
}
