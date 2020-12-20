package com.microee.traditex.liqui.oem.rmi;

import java.util.List;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import com.microee.plugin.response.R;
import com.microee.traditex.inbox.oem.hbitex.vo.AccountBalanceList;

public interface ILiquiDefaultRMi {

    // 获取当前价格汇率
    @RequestMapping(value = "/getPricing", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<?> getPricing();

    // 获取当前价格汇率
    @Deprecated
    @RequestMapping(value = "/getBalance", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<Map<Object, Object>> getBalance(@RequestParam("type") String type);

    // 获取当前账户币种余额
    @RequestMapping(value = "/getBalances", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<String> getBalances(@RequestParam("type") String type, @RequestParam("currency") String[] currency);
    
    // 获取当前账户币种余额
    @RequestMapping(value = "/getAccountBalances", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<List<AccountBalanceList>> getAccountBalances(@RequestParam("type") String type, @RequestParam("currency") String[] currency);
}
