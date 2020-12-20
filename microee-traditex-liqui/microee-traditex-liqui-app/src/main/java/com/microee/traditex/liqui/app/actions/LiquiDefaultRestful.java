package com.microee.traditex.liqui.app.actions;

import java.math.BigDecimal;
import java.time.Instant;
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
import com.fasterxml.jackson.core.type.TypeReference;
import com.microee.plugin.response.R;
import com.microee.traditex.inbox.oem.constrants.OrderSideEnum;
import com.microee.traditex.inbox.oem.hbitex.vo.AccountBalanceList;
import com.microee.traditex.inbox.rmi.TradiTexConnectorClient;
import com.microee.traditex.inbox.rmi.TradiTexHBiTexAccountClient;
import com.microee.traditex.liqui.app.components.AccountBalanceService;
import com.microee.traditex.liqui.app.components.PricingStreamService;
import com.microee.traditex.liqui.app.core.conn.ConnectBootstrap;
import com.microee.traditex.liqui.app.core.conn.ConnectServiceMap;
import com.microee.traditex.liqui.app.core.hedg.HedgingService;
import com.microee.traditex.liqui.app.props.conf.HBiTexAccountConf;
import com.microee.traditex.liqui.oem.enums.LiquiAccountType;
import com.microee.traditex.liqui.oem.rmi.ILiquiDefaultRMi;

@RestController
@RequestMapping("/")
public class LiquiDefaultRestful implements ILiquiDefaultRMi {

    @Autowired(required = false)
    private TradiTexConnectorClient tradiTexConnectorClient;

    @Autowired
    private PricingStreamService pricingStreamService;

    @Autowired
    private AccountBalanceService balanceService;

    @Autowired
    private ConnectBootstrap connectService;

    @Autowired
    private TradiTexHBiTexAccountClient hbiTexAccountClient;

    @Autowired
    private ConnectServiceMap connectServiceMap;

    @Autowired
    private HedgingService hedgingService;

    @Autowired
    private AccountBalanceService accountBalanceService;
    
    // #### 生成一个连接ID
    @RequestMapping(value = "/genid", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<String> genid() {
        return tradiTexConnectorClient.genid();
    }

    // 获取当前价格汇率
    @RequestMapping(value = "/getPricing", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<?> getPricing() {
        return R.ok(pricingStreamService.getPricing());
    }

    // 获取当前账户余额
    @Deprecated
    @RequestMapping(value = "/getBalance", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<Map<Object, Object>> getBalance(@RequestParam("type") String type) {
        LiquiAccountType accountType = null;
        if (type.equals("disk")) {
            accountType = LiquiAccountType.DISK;
        }
        if (type.equals("solr")) {
            accountType = LiquiAccountType.SOLR;
        }
        return R.ok(balanceService.getBalance(accountType));
    }

    // 获取当前账户币种余额
    @RequestMapping(value = "/getBalances", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<String> getBalances(@RequestParam("type") String type,
            @RequestParam("currency") String[] currency) {
        Assertions.assertThat(type).withFailMessage("%s 必传", "type").isNotBlank();
        Assertions.assertThat(currency).withFailMessage("%s 必传", "currency").hasSizeGreaterThan(0);
        String connid = null;
        R<List<AccountBalanceList>> result = null;
        if (type.equals("disk")) {
            connid = connectService.getConnidDiskConnid();
            Assertions.assertThat(connid).withFailMessage("%s 必传", "摆盘账户连接尚未建立").isNotNull();
        }
        if (type.equals("solr")) {
            connid = connectService.getConnidSolrConnid();
            Assertions.assertThat(connid).withFailMessage("%s 必传", "甩单账户连接尚未建立").isNotNull();
        }
        HBiTexAccountConf accountConf = connectServiceMap.getAccountByConnId(connid,
                new TypeReference<HBiTexAccountConf>() {});
        Assertions.assertThat(connid).withFailMessage("%s 必传", "type 有误").isNotNull();
        Assertions.assertThat(connid).withFailMessage("%s 必传", "restHost 未配置").isNotNull();
        result = hbiTexAccountClient.balance(connid, accountConf.getResthost(), currency);
        AccountBalanceList account = result.getData().stream()
                .filter(f -> f.getType().equals("trade")).findFirst().orElse(null);
        return R.ok(account == null ? null : account.getBalance());
    }

    // 获取当前账户币种余额
    @RequestMapping(value = "/getAccountBalances", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<List<AccountBalanceList>> getAccountBalances(@RequestParam("type") String type,
            @RequestParam("currency") String[] currency) {
        Assertions.assertThat(type).withFailMessage("%s 必传", "type").isNotBlank();
        Assertions.assertThat(currency).withFailMessage("%s 必传", "currency").hasSizeGreaterThan(0);
        List<AccountBalanceList> result = accountBalanceService.queryAccountBalance(type.equals("disk") ? LiquiAccountType.DISK : LiquiAccountType.SOLR);
        return R.ok(result.stream()
                .filter(f -> f.getType().equals("trade") || f.getType().equals("frozen"))
                .collect(Collectors.toList()));
    }

    // 查看已经建立的所有连接
    @RequestMapping(value = "/connectors", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<Map<String, String>> connectors() {
        return R.ok(connectServiceMap.connidMaps());
    }

    // 创建多空对冲订单
    @RequestMapping(value = "/hedging", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<Boolean> hedging(@RequestParam("diskClientOrderId") String diskClientOrderId,
            @RequestParam("matchId") String matchId, @RequestParam("side") String side,
            @RequestParam("filledCashMoney") BigDecimal filledCashMoney) {
        hedgingService.createOrder(diskClientOrderId, matchId, Instant.now().toEpochMilli(), OrderSideEnum.valueOf(side), filledCashMoney); 
        return R.ok(true);
    }

}
