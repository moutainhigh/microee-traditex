package com.microee.traditex.inbox.app.actions;

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
import com.microee.traditex.inbox.oem.connector.TradiTexConnection;
import com.microee.traditex.inbox.oem.cumberland.apiresult.CBApiResultForReferenceData;
import com.microee.traditex.inbox.oem.cumberland.apiresult.CBApiResultForStatus;
import com.microee.traditex.inbox.oem.cumberland.apiresult.CBApiResultForTime;
import com.microee.traditex.inbox.oem.cumberland.wsmessage.CBTradeHistoryResponseEvent;
import com.microee.traditex.inbox.oem.cumberland.wsmessage.CBTradeRequestParam;
import com.microee.traditex.inbox.oem.cumberland.wsmessage.CBTradeResponseEvent;
import com.microee.traditex.inbox.oem.cumberland.wsmessage.StreamQuoteEvent;
import com.microee.traditex.inbox.up.cumberland.CumberLandFactory;

@RestController
@RequestMapping("/cumberland")
public class TradiTexCumberLandRestful {

    @Autowired
    private TradiTexConnectComponent connectionComponent;

    @Autowired
    private RestValidator restValidator;
    
    // ### CumberLand 系统时间
    @RequestMapping(value = "/time", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<CBApiResultForTime> time(@RequestParam("connid") String connid) {
        Assertions.assertThat(connid).withFailMessage("%s 必传", "connid").isNotBlank();
        TradiTexConnection<?> conn = connectionComponent.get(connid);
        restValidator.connIdValid(connid).connIdFun(connid, conn).connIdIllegalCumberland(connid); 
        CumberLandFactory cumberlandTradFactory = (CumberLandFactory) conn.getFactory(); 
        return R.ok(cumberlandTradFactory.proxy().getTime());
    }
    
    // ### CumberLand 系统状态
    @RequestMapping(value = "/stats", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<CBApiResultForStatus> stats(@RequestParam("connid") String connid) {
        Assertions.assertThat(connid).withFailMessage("%s 必传", "connid").isNotBlank();
        TradiTexConnection<?> conn = connectionComponent.get(connid);
        restValidator.connIdValid(connid).connIdFun(connid, conn).connIdIllegalCumberland(connid); 
        CumberLandFactory cumberlandTradFactory = (CumberLandFactory) conn.getFactory(); 
        return R.ok(cumberlandTradFactory.proxy().getStatus());
    }
    
    // ### CumberLand 查询可交易的币对
    @RequestMapping(value = "/symbols", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<CBApiResultForReferenceData> symbols(@RequestParam("connid") String connid) {
        Assertions.assertThat(connid).withFailMessage("%s 必传", "connid").isNotBlank();
        TradiTexConnection<?> conn = connectionComponent.get(connid);
        restValidator.connIdValid(connid).connIdFun(connid, conn).connIdIllegalCumberland(connid); 
        CumberLandFactory cumberlandTradFactory = (CumberLandFactory) conn.getFactory(); 
        return R.ok(cumberlandTradFactory.proxy().getNewSymbolNames());
    }
    
    // ### CumberLand 订阅交易对(cumberland是基于询价模式)
    @RequestMapping(value = "/sub", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<String> sub(@RequestParam("connid") String connid,
            @RequestParam("baseCurrency") String baseCurrency,
            @RequestParam("quoteCurrency") String quoteCurrency) {
        Assertions.assertThat(connid).withFailMessage("%s 必传", "connid").isNotBlank();
        Assertions.assertThat(baseCurrency).withFailMessage("%s 必传", "baseCurrency").isNotBlank();
        Assertions.assertThat(quoteCurrency).withFailMessage("%s 必传", "quoteCurrency").isNotBlank();
        TradiTexConnection<?> conn = connectionComponent.get(connid);
        restValidator.connIdValid(connid).connIdFun(connid, conn).connIdIllegalCumberland(connid); 
        CumberLandFactory cumberlandTradFactory = (CumberLandFactory) conn.getFactory(); 
        StreamQuoteEvent event = cumberlandTradFactory.proxy().streamingQuoteRequest(baseCurrency, quoteCurrency);
        if (event.getStatus().equals("REJECTED")) {
            return R.failed(R.FAILED, event.getReason(), "订阅失败"); 
        }
        return R.ok(event.getQuoteResponseId().getId());
    }
    
    /**
     * CumberLand 取消订阅交易对报价
     * @param connid
     * @param quoteResponstId 订阅时返回的 responseId
     * @return
     */
    @RequestMapping(value = "/unsub", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<String> unsubQuote(@RequestParam("connid") String connid,
            @RequestParam("quoteResponstId") String quoteResponstId) {
        Assertions.assertThat(connid).withFailMessage("%s 必传", "connid").isNotBlank();
        Assertions.assertThat(quoteResponstId).withFailMessage("%s 必传", "quoteResponstId").isNotBlank();
        TradiTexConnection<?> conn = connectionComponent.get(connid);
        restValidator.connIdValid(connid).connIdFun(connid, conn).connIdIllegalCumberland(connid); 
        CumberLandFactory cumberlandTradFactory = (CumberLandFactory) conn.getFactory(); 
        return R.ok(cumberlandTradFactory.proxy().unsubscribe(quoteResponstId));
    }
    
    // ### CumberLand 查新交易历史
    @RequestMapping(value = "/queryTradHistory", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<CBTradeHistoryResponseEvent> queryTradHistory(@RequestParam("connid") String connid,
            @RequestParam("count") Integer count) {
        Assertions.assertThat(connid).withFailMessage("%s 必传", "connid").isNotBlank();
        Assertions.assertThat(count).withFailMessage("%s 最少查1条", "count").isGreaterThan(0);
        Assertions.assertThat(count).withFailMessage("%s 最多查500条", "count").isLessThanOrEqualTo(500);
        TradiTexConnection<?> conn = connectionComponent.get(connid);
        restValidator.connIdValid(connid).connIdFun(connid, conn).connIdIllegalCumberland(connid); 
        CumberLandFactory cumberlandTradFactory = (CumberLandFactory) conn.getFactory(); 
        return R.ok(cumberlandTradFactory.proxy().queryTradHistory(count));
    }
    
    // 返回第三方订单号和订单状态
    // ### CumberLand 创建订单
    // REJECTED, FILLED
    @RequestMapping(value = "/newOrder", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<String> newOrder(@RequestParam("connid") String connid,
            @RequestParam("baseCurrency") String baseCurrency,
            @RequestParam("quoteCurrency") String quoteCurrency,
            @RequestBody CBTradeRequestParam param) {
        Assertions.assertThat(connid).withFailMessage("%s 必传", "connid").isNotBlank();
        Assertions.assertThat(baseCurrency).withFailMessage("%s 必传", "baseCurrency").isNotBlank();
        Assertions.assertThat(quoteCurrency).withFailMessage("%s 必传", "quoteCurrency").isNotBlank();
        Assertions.assertThat(param).withFailMessage("%s 必传", "post body").isNotNull();
        TradiTexConnection<?> conn = connectionComponent.get(connid);
        restValidator.connIdValid(connid).connIdFun(connid, conn).connIdIllegalCumberland(connid); 
        CumberLandFactory cumberlandTradFactory = (CumberLandFactory) conn.getFactory(); 
        CBTradeResponseEvent createOrderResponse = cumberlandTradFactory.proxy().createOrder(baseCurrency, quoteCurrency, param);
        return R.ok(createOrderResponse.getTradeId()).message(createOrderResponse.getStatus());
    }
    
}
