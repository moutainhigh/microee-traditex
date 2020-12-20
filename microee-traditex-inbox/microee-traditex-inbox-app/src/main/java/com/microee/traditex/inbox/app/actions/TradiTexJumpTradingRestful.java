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
import com.microee.traditex.inbox.app.components.TradiTexRedis;
import com.microee.traditex.inbox.app.validator.RestValidator;
import com.microee.traditex.inbox.oem.connector.TradiTexConnection;
import com.microee.traditex.inbox.oem.jumptrading.apiparam.JTOrderStatusParam;
import com.microee.traditex.inbox.oem.jumptrading.apiparam.JumpTradingNewOrderSingleParam;
import com.microee.traditex.inbox.oem.jumptrading.apiresult.JumpTradingApiResultForMarketData;
import com.microee.traditex.inbox.oem.jumptrading.apiresult.JumpTradingApiResultForOrderStatus;
import com.microee.traditex.inbox.oem.jumptrading.apiresult.JumpTradingResultForNewOrderSingle;
import com.microee.traditex.inbox.up.jumptrading.JumpTradingFactory;

@RestController
@RequestMapping("/jumptrading")
public class TradiTexJumpTradingRestful {

    @Autowired
    private TradiTexConnectComponent connectionComponent;

    @Autowired
    private RestValidator restValidator;

    @Autowired
    private TradiTexRedis tradiTexRedis;
    
    // ### JumpTrading 一次心跳
    @RequestMapping(value = "/heartBeat", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<Boolean> heartBeat(@RequestParam("connid") String connid) {
        Assertions.assertThat(connid).withFailMessage("%s 必传", "connid").isNotBlank();
        TradiTexConnection<?> conn = connectionComponent.get(connid);
        restValidator.connIdValid(connid).connIdFun(connid, conn).connIdIllegalJumpTrading(connid);
        JumpTradingFactory jumpTradingFactory = (JumpTradingFactory) conn.getFactory(); 
        jumpTradingFactory.proxy().heartBeat();
        return R.ok(true);
    }
    
    // ### JumpTrading 退出登录
    @RequestMapping(value = "/logout", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<Boolean> logout(@RequestParam("connid") String connid) {
        Assertions.assertThat(connid).withFailMessage("%s 必传", "connid").isNotBlank();
        TradiTexConnection<?> conn = connectionComponent.get(connid);
        restValidator.connIdValid(connid).connIdFun(connid, conn).connIdIllegalJumpTrading(connid);
        JumpTradingFactory jumpTradingFactory = (JumpTradingFactory) conn.getFactory(); 
        return R.ok(jumpTradingFactory.proxy().logout());
    }
    
    // ### JumpTrading 获取支持的交易对
    @RequestMapping(value = "/symbols", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<List<String>> symbols(@RequestParam("connid") String connid) {
        Assertions.assertThat(connid).withFailMessage("%s 必传", "connid").isNotBlank();
        TradiTexConnection<?> conn = connectionComponent.get(connid);
        restValidator.connIdValid(connid).connIdFun(connid, conn).connIdIllegalJumpTrading(connid);
        JumpTradingFactory jumpTradingFactory = (JumpTradingFactory) conn.getFactory(); 
        return R.ok(jumpTradingFactory.proxy().securityDefinitionRequest());
    }
    
    // ### JumpTrading 订阅或取消订阅交易对
    @RequestMapping(value = "/sub", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<JumpTradingApiResultForMarketData> sub(@RequestParam("connid") String connid, @RequestParam("symbol") String symbol, @RequestParam("subscribe") Boolean subscribe) {
        Assertions.assertThat(connid).withFailMessage("%s 必传", "connid").isNotBlank();
        Assertions.assertThat(symbol).withFailMessage("%s 必传", "symbols").isNotBlank();
        Assertions.assertThat(subscribe).withFailMessage("%s 必传", "subscribe").isNotNull();
        TradiTexConnection<?> conn = connectionComponent.get(connid);
        restValidator.connIdValid(connid).connIdFun(connid, conn).connIdIllegalJumpTrading(connid);
        JumpTradingFactory jumpTradingFactory = (JumpTradingFactory) conn.getFactory(); 
        JumpTradingApiResultForMarketData result = jumpTradingFactory.proxy().marketDataRequest(subscribe, symbol);
        if (subscribe) {
            jumpTradingFactory.addTopic(symbol);
        } else {
            jumpTradingFactory.removeTopics(symbol);
        }
        tradiTexRedis.writeConnection(connid, jumpTradingFactory);
        return R.ok(result);
    }
    
    // ### JumpTrading 查询订单状态
    @RequestMapping(value = "/orderStats", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<JumpTradingApiResultForOrderStatus> orderStats(@RequestParam("connid") String connid, @RequestBody JTOrderStatusParam param) {
        Assertions.assertThat(connid).withFailMessage("%s 必传", "connid").isNotBlank();
        Assertions.assertThat(param).withFailMessage("%s 必传", "post body").isNotNull();
        TradiTexConnection<?> conn = connectionComponent.get(connid);
        restValidator.connIdValid(connid).connIdFun(connid, conn).connIdIllegalJumpTrading(connid);
        JumpTradingFactory jumpTradingFactory = (JumpTradingFactory) conn.getFactory(); 
        return R.ok(jumpTradingFactory.proxy().orderStatus(param));
    }
    
    // ### JumpTrading 创建订单
    // Status: '0' (New), '1' (PartialFill'), '2' (Filled), '3' (DoneForDay), '4' (Cancelled) or '8' (Rejected)
    @RequestMapping(value = "/newOrder", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<JumpTradingResultForNewOrderSingle> newOrder(@RequestParam("connid") String connid, @RequestBody JumpTradingNewOrderSingleParam param) {
        Assertions.assertThat(connid).withFailMessage("%s 必传", "connid").isNotBlank();
        Assertions.assertThat(param).withFailMessage("%s 必传", "post body").isNotNull();
        TradiTexConnection<?> conn = connectionComponent.get(connid);
        restValidator.connIdValid(connid).connIdFun(connid, conn).connIdIllegalJumpTrading(connid);
        JumpTradingFactory jumpTradingFactory = (JumpTradingFactory) conn.getFactory(); 
        return R.ok(jumpTradingFactory.proxy().newOrderSingle(param));
    }
    
}
