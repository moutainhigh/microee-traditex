package com.microee.traditex.inbox.oem.rmi;

import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import com.microee.plugin.response.R;
import com.microee.traditex.inbox.oem.hbitex.vo.AccountBalanceList;

public interface ITradiTexHBiTexAccountRMi {

    // #### account
    // 查询账户id
    @RequestMapping(value = "/spot-account-id", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<String> spotAccountId(@RequestParam("connid") String connid,
            @RequestParam("resthost") String resthost);

    // #### account
    // 查询账户余额
    @RequestMapping(value = "/balance", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<List<AccountBalanceList>> balance(@RequestParam("connid") String connid,
            @RequestParam("resthost") String resthost, @RequestParam("currency") String[] currency);

}
