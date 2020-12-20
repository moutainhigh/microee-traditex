package com.microee.traditex.inbox.app.actions;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.microee.plugin.response.R;
import com.microee.traditex.inbox.app.components.TradiTexConnectComponent;
import com.microee.traditex.inbox.app.components.TradiTexRedis;
import com.microee.traditex.inbox.app.validator.RestValidator;
import com.microee.traditex.inbox.oem.connector.TradiTexConnection;
import com.microee.traditex.inbox.oem.hbitex.HBiTexHttpResult;
import com.microee.traditex.inbox.oem.hbitex.vo.AccountBalance;
import com.microee.traditex.inbox.oem.hbitex.vo.AccountBalanceList;
import com.microee.traditex.inbox.oem.rmi.ITradiTexHBiTexAccountRMi;
import com.microee.traditex.inbox.up.hbitex.HBiTexTradFactory;
import okhttp3.Headers;

// HBiTex 账户相关
@RestController
@RequestMapping("/traditex-hbitex-account")
public class TradiTexHBiTexAccountRestful implements ITradiTexHBiTexAccountRMi {

    @Autowired
    private TradiTexConnectComponent connectionComponent;

    @Autowired
    private RestValidator restValidator;

    @Autowired
    private TradiTexRedis tradiTexRedis;
    
    // #### account
    // 查询账户id
    @RequestMapping(value = "/spot-account-id", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<String> spotAccountId(@RequestParam("connid") String connid,
            @RequestParam("resthost") String resthost) {
        Assertions.assertThat(connid).withFailMessage("%s 必传", "connid").isNotBlank();
        Assertions.assertThat(resthost).withFailMessage("%s 必传", "resthost").isNotBlank();
        restValidator.connIdValid(connid);
        TradiTexConnection<?> conn = connectionComponent.get(connid);
        restValidator.connIdFun(connid, conn).connIdIllegalHbiTex(connid);
        HBiTexTradFactory hbiTexTradFactory =
                connectionComponent.get(connid, HBiTexTradFactory.class);
        HBiTexHttpResult<List<Map<String, Object>>> hbiTexResult =
                hbiTexTradFactory.queryAccounts(resthost);
        if (hbiTexResult == null) {
            return R.failed(null);
        }
        if (!hbiTexResult.isSuccess()) {
            return R.failed(hbiTexResult.getErrCode() + "`" + hbiTexResult.getErrMsg() + "`");
        }
        if (hbiTexResult.getData() == null || hbiTexResult.getData().size() == 0) {
            return R.failed(null);
        }
        String spotAccountId = null;
        for (int i = 0; i < hbiTexResult.getData().size(); i++) {
            Map<String, Object> map = hbiTexResult.getData().get(i);
            if (map.containsKey("type") && map.containsKey("id")) {
                if (map.get("type").toString().equals("spot")
                        && map.get("state").toString().equals("working")) {
                    spotAccountId = map.get("id").toString();
                    break;
                }
            }
        }
        if (spotAccountId != null) {
            hbiTexTradFactory.addHeaders(Headers.of("accountId", spotAccountId));
            tradiTexRedis.writeConnection(connid, hbiTexTradFactory);
        }
        return R.ok(spotAccountId);
    }

    // #### account
    // 查询账户余额
    @RequestMapping(value = "/balance", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<List<AccountBalanceList>> balance(@RequestParam("connid") String connid,
            @RequestParam("resthost") String resthost, @RequestParam("currency") String[] currency) {
        Assertions.assertThat(connid).withFailMessage("%s 必传", "connid").isNotBlank();
        Assertions.assertThat(resthost).withFailMessage("%s 必传", "resthost").isNotBlank();
        restValidator.connIdValid(connid);
        TradiTexConnection<?> conn = connectionComponent.get(connid);
        restValidator.connIdFun(connid, conn).connIdIllegalHbiTex(connid);
        HBiTexTradFactory hbiTexTradFactory =
                connectionComponent.get(connid, HBiTexTradFactory.class);
        HBiTexHttpResult<AccountBalance> hbiTexResult =
                hbiTexTradFactory.querySpotAccountBalance(resthost);
        if (!hbiTexResult.isSuccess()) {
            return R.failed(hbiTexResult.getErrCode() + "`" + hbiTexResult.getErrMsg() + "`");
        }
        return R.ok(hbiTexResult.getData().getList().stream()
                .filter(f ->  Arrays.stream(currency).anyMatch(f.getCurrency()::equals)).collect(Collectors.toList()));
    }

}
