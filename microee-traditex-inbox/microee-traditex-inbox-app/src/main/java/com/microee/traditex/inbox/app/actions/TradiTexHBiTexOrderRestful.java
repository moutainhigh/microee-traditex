package com.microee.traditex.inbox.app.actions;

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
import com.microee.plugin.response.exception.RestException;
import com.microee.traditex.inbox.app.components.TradiTexConnectComponent;
import com.microee.traditex.inbox.app.validator.RestValidator;
import com.microee.traditex.inbox.oem.connector.TradiTexConnection;
import com.microee.traditex.inbox.oem.hbitex.HBiTexHttpResult;
import com.microee.traditex.inbox.oem.hbitex.po.BigDecimalValue;
import com.microee.traditex.inbox.oem.hbitex.po.HBiTexOrderPlaceParam;
import com.microee.traditex.inbox.oem.hbitex.vo.HBiTexOrderDetails;
import com.microee.traditex.inbox.oem.hbitex.vo.OrderMatchresults;
import com.microee.traditex.inbox.oem.rmi.ITradiTexHBiTexOrderRMi;
import com.microee.traditex.inbox.up.hbitex.HBiTexTradFactory;

//HBiTex 订单相关
@RestController
@RequestMapping("/traditex-hbitex-order")
public class TradiTexHBiTexOrderRestful implements ITradiTexHBiTexOrderRMi {

    @Autowired
    private TradiTexConnectComponent connectionComponent;
    
    @Autowired
    private RestValidator restValidator;
    
    // #### symbols
    // 查询交易对
    @RequestMapping(value = "/symbols", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<List<String>> symbols(
            @RequestParam("connid") String connid,
            @RequestParam("resthost") String resthost) {
        Assertions.assertThat(connid).withFailMessage("%s 必传", "connid").isNotBlank();
        restValidator.connIdValid(connid);
        TradiTexConnection<?> conn = connectionComponent.get(connid);
        restValidator.connIdFun(connid, conn).connIdIllegalHbiTex(connid);
        HBiTexTradFactory hbiTexTradFactory = connectionComponent.get(connid, HBiTexTradFactory.class);
        HBiTexHttpResult<List<Map<String, Object>>> hbiTexResult = hbiTexTradFactory.querySymbols(resthost);
        if (hbiTexResult == null) {
            return R.failed(R.TIME_OUT, "超时");
        }
        if (!hbiTexResult.isSuccess()) {
            return R.failed(hbiTexResult.getErrCode() + "`" + hbiTexResult.getErrMsg() + "`");
        }
        List<String> result = hbiTexResult.getData().stream().filter(f -> f.get("state").toString().equals("online")).map(m -> {
            return String.format("%s/%s", m.get("base-currency"), m.get("quote-currency"));
        }).collect(Collectors.toList());
        return R.ok(result);
    }

    // #### symbols
    // 查询交易对
    @RequestMapping(value = "/symbol/get", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<Map<String, Object>> symbol(
            @RequestParam("connid") String connid,
            @RequestParam("resthost") String resthost,
            @RequestParam("symbol") String symbol) {
        Assertions.assertThat(connid).withFailMessage("%s 必传", "connid").isNotBlank();
        restValidator.connIdValid(connid);
        TradiTexConnection<?> conn = connectionComponent.get(connid);
        restValidator.connIdFun(connid, conn).connIdIllegalHbiTex(connid);
        HBiTexTradFactory hbiTexTradFactory = connectionComponent.get(connid, HBiTexTradFactory.class);
        HBiTexHttpResult<List<Map<String, Object>>> hbiTexResult = hbiTexTradFactory.querySymbols(resthost);
        if (hbiTexResult == null) {
            return R.failed(R.TIME_OUT, "超时");
        }
        if (!hbiTexResult.isSuccess()) {
            return R.failed(hbiTexResult.getErrCode() + "`" + hbiTexResult.getErrMsg() + "`");
        }
        return R.ok(hbiTexResult.getData().stream().filter(f -> f.get("symbol").toString().equals(symbol)).findFirst().orElse(null));
    }
    
    // #### order
    // # 时间同步
    // $ sudo ntpdate time.ntp.org
    // $ date
    // 创建订单
    @RequestMapping(value = "/create", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<HBiTexHttpResult<String>> create(
            @RequestParam("connid") String connid,
            @RequestParam("client-order-id") String clientOrderId,
            @RequestParam("order-type") String orderType,
            @RequestParam("symbol") String symbol,
            @RequestParam(value = "price", required=false) String price, // 市价单无价格
            @RequestParam("amount") String amount,
            @RequestParam("side") String side, // BUY or SELL
            @RequestParam("resthost") String resthost) {
        Assertions.assertThat(connid).withFailMessage("%s 必传", "connid").isNotBlank();
        Assertions.assertThat(clientOrderId).withFailMessage("%s 必传", "client-order-id").isNotBlank();
        Assertions.assertThat(orderType).withFailMessage("%s 必传", "order-type").isNotBlank();
        Assertions.assertThat(symbol).withFailMessage("%s 必传", "symbol").isNotBlank();
        //Assertions.assertThat(price).withFailMessage("%s 必传", "price").isNotBlank();
        Assertions.assertThat(amount).withFailMessage("%s 必传", "amount").isNotBlank();
        Assertions.assertThat(side).withFailMessage("%s 必传", "side").isNotBlank();
        Assertions.assertThat(resthost).withFailMessage("%s 必传", "resthost").isNotBlank();
        BigDecimalValue priceDecimal = price == null ? null : new BigDecimalValue(price);
        BigDecimalValue amountDecimal = new BigDecimalValue(amount);
        // Assertions.assertThat(priceDecimal.value).withFailMessage("price `%s` 格式有误", price).isNotNull();
        Assertions.assertThat(amountDecimal.value).withFailMessage("amount `%s` 格式有误", amount).isNotNull();
        restValidator.connIdValid(connid);
        TradiTexConnection<?> conn = connectionComponent.get(connid);
        restValidator.connIdFun(connid, conn).connIdIllegalHbiTex(connid);
        HBiTexTradFactory hbiTexTradFactory = connectionComponent.get(connid, HBiTexTradFactory.class);
        HBiTexHttpResult<String>  result = hbiTexTradFactory.createOrder(resthost, new HBiTexOrderPlaceParam(symbol, side, amountDecimal.value, priceDecimal == null ? null : priceDecimal.value, clientOrderId, orderType));
        if (result == null) {
            throw new RestException(R.TIME_OUT, "超时");
        }
        return R.ok(result);
    }

    // #### order
    // 查询订单详情
    @RequestMapping(value = "/details", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<HBiTexOrderDetails> details(
            @RequestParam("connid") String connid,
            @RequestParam("resthost") String resthost,
            @RequestParam("order-id") String orderId) {
        Assertions.assertThat(connid).withFailMessage("%s 必传", "connid").isNotBlank();
        Assertions.assertThat(resthost).withFailMessage("%s 必传", "resthost").isNotBlank();
        Assertions.assertThat(orderId).withFailMessage("%s 必传", "order-id").isNotBlank();
        restValidator.connIdValid(connid);
        TradiTexConnection<?> conn = connectionComponent.get(connid);
        restValidator.connIdFun(connid, conn).connIdIllegalHbiTex(connid);
        HBiTexTradFactory hbiTexTradFactory = connectionComponent.get(connid, HBiTexTradFactory.class);
        HBiTexHttpResult<HBiTexOrderDetails> hbiTexResult = hbiTexTradFactory.queryOrderDetail(resthost, orderId);
        if (hbiTexResult == null) {
            return R.failed(R.TIME_OUT, "超时");
        }
        if (!hbiTexResult.isSuccess()) {
            return R.failed(hbiTexResult.getErrCode() + "`" + hbiTexResult.getErrMsg() + "`");
        }
        return R.ok(hbiTexResult.getData());
    }

    // #### orders
    // 历史订单
    @RequestMapping(value = "/orders", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<List<HBiTexOrderDetails>> orders(
            @RequestParam("connid") String connid,
            @RequestParam("resthost") String resthost,
            @RequestParam("symbol") String symbol,
            @RequestParam("stats") String stats) {
        Assertions.assertThat(connid).withFailMessage("%s 必传", "connid").isNotBlank();
        Assertions.assertThat(resthost).withFailMessage("%s 必传", "resthost").isNotBlank();
        Assertions.assertThat(symbol).withFailMessage("%s 必传", "symbol").isNotBlank();
        restValidator.connIdValid(connid);
        TradiTexConnection<?> conn = connectionComponent.get(connid);
        restValidator.connIdFun(connid, conn).connIdIllegalHbiTex(connid);
        HBiTexTradFactory hbiTexTradFactory = connectionComponent.get(connid, HBiTexTradFactory.class);
        HBiTexHttpResult<List<HBiTexOrderDetails>> hbiTexResult = hbiTexTradFactory.queryOrders(resthost, symbol, stats);
        if (hbiTexResult == null) {
            return R.failed(R.TIME_OUT, "超时");
        }
        if (!hbiTexResult.isSuccess()) {
            return R.failed(hbiTexResult.getErrCode() + "`" + hbiTexResult.getErrMsg() + "`");
        }
        return R.ok(hbiTexResult.getData());
    }

    // #### order
    // 根据客户端订单id查询订单详情
    @RequestMapping(value = "/queryOrder", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<HBiTexOrderDetails> queryOrder(
            @RequestParam("connid") String connid,
            @RequestParam("resthost") String resthost,
            @RequestParam("client-order-id") String clientOrderId) {
        Assertions.assertThat(connid).withFailMessage("%s 必传", "connid").isNotBlank();
        Assertions.assertThat(resthost).withFailMessage("%s 必传", "resthost").isNotBlank();
        Assertions.assertThat(clientOrderId).withFailMessage("%s 必传", "client-order-id").isNotBlank();
        restValidator.connIdValid(connid);
        TradiTexConnection<?> conn = connectionComponent.get(connid);
        restValidator.connIdFun(connid, conn).connIdIllegalHbiTex(connid);
        HBiTexTradFactory hbiTexTradFactory = connectionComponent.get(connid, HBiTexTradFactory.class);
        HBiTexHttpResult<HBiTexOrderDetails> hbiTexResult = hbiTexTradFactory.queryOrderByClientId(resthost, clientOrderId); 
        if (hbiTexResult == null) {
            return R.failed(R.TIME_OUT, "超时");
        }
        if (!hbiTexResult.isSuccess()) {
            if (hbiTexResult.getErrCode().equals("base-record-invalid")) {
                return R.ok(null);
            }
            return R.failed(hbiTexResult.getErrCode() + "`" + hbiTexResult.getErrMsg() + "`");
        }
        return R.ok(hbiTexResult.getData());
    }

    // #### order
    // 查询订单撮合详情
    @RequestMapping(value = "/matchs", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<List<OrderMatchresults>> matchs(
            @RequestParam("connid") String connid,
            @RequestParam("resthost") String resthost,
            @RequestParam("order-id") String orderId) {
        Assertions.assertThat(connid).withFailMessage("%s 必传", "connid").isNotBlank();
        Assertions.assertThat(resthost).withFailMessage("%s 必传", "resthost").isNotBlank();
        Assertions.assertThat(orderId).withFailMessage("%s 必传", "order-id").isNotBlank();
        restValidator.connIdValid(connid);
        TradiTexConnection<?> conn = connectionComponent.get(connid);
        restValidator.connIdFun(connid, conn).connIdIllegalHbiTex(connid);
        HBiTexTradFactory hbiTexTradFactory = connectionComponent.get(connid, HBiTexTradFactory.class);
        HBiTexHttpResult<List<OrderMatchresults>> hbiTexResult = hbiTexTradFactory.queryOrderMatchresults(resthost, orderId);
        if (hbiTexResult == null) {
            return R.failed(R.TIME_OUT, "超时");
        }
        if (!hbiTexResult.isSuccess()) {
            return R.failed(hbiTexResult.getErrCode() + "`" + hbiTexResult.getErrMsg() + "`");
        }
        return R.ok(hbiTexResult.getData());
    }

    // #### order
    // 撤单
    @RequestMapping(value = "/revoke", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<?> revoke(
            @RequestParam("connid") String connid,
            @RequestParam("resthost") String resthost,
            @RequestParam("order-ids") String[] orderId) {
        Assertions.assertThat(connid).withFailMessage("%s 必传", "connid").isNotBlank();
        Assertions.assertThat(resthost).withFailMessage("%s 必传", "resthost").isNotBlank();
        Assertions.assertThat(orderId).withFailMessage("%s 必传", "order-id").isNotNull();
        Assertions.assertThat(orderId).withFailMessage("%s 空", "order-id").hasSizeLessThanOrEqualTo(50);
        restValidator.connIdValid(connid);
        TradiTexConnection<?> conn = connectionComponent.get(connid);
        restValidator.connIdFun(connid, conn).connIdIllegalHbiTex(connid);
        HBiTexTradFactory hbiTexTradFactory = connectionComponent.get(connid, HBiTexTradFactory.class);
        if (orderId.length == 1) {
            HBiTexHttpResult<String> hbiTexResult = hbiTexTradFactory.revokeOrder(resthost, orderId[0]);
            if (hbiTexResult == null) {
                return R.failed(R.TIME_OUT, "超时");
            }
            if (!hbiTexResult.isSuccess()) {
                return R.failed(hbiTexResult.getErrCode() + "`" + hbiTexResult.getErrMsg() + "`").message(hbiTexResult.getErrCode());
            }
            return R.ok(hbiTexResult.getData());
        }
        // 批量撤单
        HBiTexHttpResult<Map<String, Object>> hbiTexResult = hbiTexTradFactory.revokeOrder(resthost, orderId);
        if (hbiTexResult == null) {
            return R.failed(R.TIME_OUT, "超时");
        }
        if (!hbiTexResult.isSuccess()) {
            return R.failed(hbiTexResult.getErrCode() + "`" + hbiTexResult.getErrMsg() + "`").message(hbiTexResult.getErrCode());
        }
        return R.ok(hbiTexResult.getData().get("success"));
    }
    
}
