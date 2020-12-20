package com.microee.traditex.dashboard.app.actions;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.microee.plugin.response.R;
import com.microee.traditex.inbox.oem.hbitex.vo.AccountBalanceList;
import com.microee.traditex.liqui.rmi.LiquiDefaultClient;

@RestController
@RequestMapping("/trads")
public class TradRestful {

    @Autowired
    private LiquiDefaultClient liquiDefaultClient;

    // 获取当前价格汇率
    @RequestMapping(value = "/getPricing", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<?> getPricing() {
        return liquiDefaultClient.getPricing();
    }
    
//    // 获取当前账户余额
//    @RequestMapping(value = "/getBalance", method = RequestMethod.GET,
//            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
//    public R<Map<Object, Object>> getBalance(@RequestParam("type") String type) {
//        return liquiDefaultClient.getBalance(type);
//    }
    
    // 获取当前账户余额
    @RequestMapping(value = "/getBalances", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<String> getBalances(@RequestParam("type") String type, @RequestParam("currency") String[] currency) {
        return liquiDefaultClient.getBalances(type, currency);
    }

    // 获取当前账户余额
    @RequestMapping(value = "/getAccountBalances", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<List<AccountBalanceList>> getAccountBalances(@RequestParam("type") String type, @RequestParam("currency") String[] currency) {
        return liquiDefaultClient.getAccountBalances(type, currency);
    }
    
}
