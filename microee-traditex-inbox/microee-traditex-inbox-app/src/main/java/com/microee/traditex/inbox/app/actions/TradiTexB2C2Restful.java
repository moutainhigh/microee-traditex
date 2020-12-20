package com.microee.traditex.inbox.app.actions;

import java.util.List;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.microee.plugin.response.R;
import com.microee.traditex.inbox.app.components.TradiTexConnectComponent;
import com.microee.traditex.inbox.app.validator.RestValidator;
import com.microee.traditex.inbox.oem.b2c2.apiparam.B2C2NewOrderParam;
import com.microee.traditex.inbox.oem.b2c2.apiresult.B2C2ApiResultForAccountInfo;
import com.microee.traditex.inbox.oem.b2c2.apiresult.B2C2ApiResultForBalance;
import com.microee.traditex.inbox.oem.b2c2.apiresult.B2C2ApiResultForCurrency;
import com.microee.traditex.inbox.oem.b2c2.apiresult.B2C2ApiResultForInstruments;
import com.microee.traditex.inbox.oem.b2c2.apiresult.B2C2ApiResultForNewOrder;
import com.microee.traditex.inbox.oem.connector.TradiTexConnection;
import com.microee.traditex.inbox.up.b2c2.B2C2Factory;

@RestController
@RequestMapping("/b2c2")
public class TradiTexB2C2Restful {

    @Autowired
    private TradiTexConnectComponent connectionComponent;

    @Autowired
    private RestValidator restValidator;
    
    // ### B2C2 查询可交易的币对
    @RequestMapping(value = "/instruments", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<List<B2C2ApiResultForInstruments>> instruments(@RequestParam("connid") String connid) {
        Assertions.assertThat(connid).withFailMessage("%s 必传", "connid").isNotBlank();
        TradiTexConnection<?> conn = connectionComponent.get(connid);
        restValidator.connIdValid(connid).connIdFun(connid, conn).connIdIllegalB2C2(connid);
        B2C2Factory b2c2Factory = (B2C2Factory) conn.getFactory(); 
        return R.ok(b2c2Factory.proxy().getInstruments());
    }

    // ### B2C2 查询账户余额
    @RequestMapping(value = "/balance", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<B2C2ApiResultForBalance> balance(@RequestParam("connid") String connid) {
        Assertions.assertThat(connid).withFailMessage("%s 必传", "connid").isNotBlank();
        TradiTexConnection<?> conn = connectionComponent.get(connid);
        restValidator.connIdValid(connid).connIdFun(connid, conn).connIdIllegalB2C2(connid);
        B2C2Factory b2c2Factory = (B2C2Factory) conn.getFactory(); 
        return R.ok(b2c2Factory.proxy().getBalance());
    }

    // ### B2C2 查询币种
    @RequestMapping(value = "/currency", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<B2C2ApiResultForCurrency> currency(@RequestParam("connid") String connid) {
        Assertions.assertThat(connid).withFailMessage("%s 必传", "connid").isNotBlank();
        TradiTexConnection<?> conn = connectionComponent.get(connid);
        restValidator.connIdValid(connid).connIdFun(connid, conn).connIdIllegalB2C2(connid);
        B2C2Factory b2c2Factory = (B2C2Factory) conn.getFactory(); 
        return R.ok(b2c2Factory.proxy().getCurrency());
    }

    // ### B2C2 查询账户信息
    @RequestMapping(value = "/account", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<B2C2ApiResultForAccountInfo> account(@RequestParam("connid") String connid) {
        Assertions.assertThat(connid).withFailMessage("%s 必传", "connid").isNotBlank();
        TradiTexConnection<?> conn = connectionComponent.get(connid);
        restValidator.connIdValid(connid).connIdFun(connid, conn).connIdIllegalB2C2(connid);
        B2C2Factory b2c2Factory = (B2C2Factory) conn.getFactory(); 
        return R.ok(b2c2Factory.proxy().getAccountInfo());
    }

    // ### B2C2 查询订单
    @RequestMapping(value = "/queryOrder", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<List<B2C2ApiResultForNewOrder>> queryOrder(@RequestParam("connid") String connid,
            @RequestParam("orderId") String orderId) {
        Assertions.assertThat(connid).withFailMessage("%s 必传", "connid").isNotBlank();
        Assertions.assertThat(orderId).withFailMessage("%s 必传", "orderId").isNotBlank();
        TradiTexConnection<?> conn = connectionComponent.get(connid);
        restValidator.connIdValid(connid).connIdFun(connid, conn).connIdIllegalB2C2(connid);
        B2C2Factory b2c2Factory = (B2C2Factory) conn.getFactory(); 
        return R.ok(b2c2Factory.proxy().queryOrder(orderId));
    }

    // ### B2C2 订阅或取消订阅交易对
    @RequestMapping(value = "/sub", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<String> sub(@RequestParam("connid") String connid,
            @RequestParam("baseCurrency") String baseCurrency,
            @RequestParam("quoteCurrency") String quoteCurrency,
            @RequestParam("subscribe") Boolean subscribe) {
        Assertions.assertThat(connid).withFailMessage("%s 必传", "connid").isNotBlank();
        Assertions.assertThat(baseCurrency).withFailMessage("%s 必传", "baseCurrency").isNotBlank();
        Assertions.assertThat(quoteCurrency).withFailMessage("%s 必传", "quoteCurrency").isNotBlank();
        TradiTexConnection<?> conn = connectionComponent.get(connid);
        restValidator.connIdValid(connid).connIdFun(connid, conn).connIdIllegalB2C2(connid);
        B2C2Factory b2c2Factory = (B2C2Factory) conn.getFactory(); 
        String instrument = b2c2Factory.proxy().subscribe(baseCurrency, quoteCurrency, subscribe);
        return R.ok(instrument);
    }

    // ### B2C2 创建订单 (B2C2的订单状态是在下单时同步返回的)
    // created, partial-filled, filled, canceled
    @RequestMapping(value = "/newOrder", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<B2C2ApiResultForNewOrder> newOrder(@RequestParam("connid") String connid,
            @RequestBody B2C2NewOrderParam param) {
        Assertions.assertThat(connid).withFailMessage("%s 必传", "connid").isNotBlank();
        Assertions.assertThat(param).withFailMessage("%s 必传", "post body").isNotNull();
        TradiTexConnection<?> conn = connectionComponent.get(connid);
        restValidator.connIdValid(connid).connIdFun(connid, conn).connIdIllegalB2C2(connid);
        B2C2Factory hbiTexTradFactory = (B2C2Factory) conn.getFactory(); 
        B2C2ApiResultForNewOrder newOrderResult = hbiTexTradFactory.proxy().newOrder(param);
        // String thirdOrderId = newOrderResult.getOrderId();
        // 需通过订单成交数量判断订单状态
        return R.ok(newOrderResult);
    }
    
}
