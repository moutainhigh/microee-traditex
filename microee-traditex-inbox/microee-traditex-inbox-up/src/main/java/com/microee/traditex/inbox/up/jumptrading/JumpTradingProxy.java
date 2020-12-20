package com.microee.traditex.inbox.up.jumptrading;

import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.microee.plugin.http.assets.HttpAssets;
import com.microee.plugin.response.R;
import com.microee.traditex.inbox.oem.jumptrading.apiparam.JTMarketDataParam;
import com.microee.traditex.inbox.oem.jumptrading.apiparam.JTOrderStatusParam;
import com.microee.traditex.inbox.oem.jumptrading.apiparam.JumpTradingNewOrderSingleParam;
import com.microee.traditex.inbox.oem.jumptrading.apiresult.JumpTradingApiResultBase;
import com.microee.traditex.inbox.oem.jumptrading.apiresult.JumpTradingApiResultForHeartbeat;
import com.microee.traditex.inbox.oem.jumptrading.apiresult.JumpTradingApiResultForMarketData;
import com.microee.traditex.inbox.oem.jumptrading.apiresult.JumpTradingApiResultForOrderStatus;
import com.microee.traditex.inbox.oem.jumptrading.apiresult.JumpTradingApiResultForSecurityRequest;
import com.microee.traditex.inbox.oem.jumptrading.apiresult.JumpTradingResultForNewOrderSingle;

public class JumpTradingProxy {

    private static final Logger LOGGER = LoggerFactory.getLogger(JumpTradingProxy.class);
    private static final int HEART_PERIOD = 55; // 心跳连接频率，单位：秒, 时间太短会触发 Connection reset

    private JumpTradingFactory factory;

    public JumpTradingProxy(JumpTradingFactory factory) {
        this.factory = factory;
    }

    /**
     * 心跳
     */
    public void heartBeat() {
        JumpTradingApiResultForHeartbeat apiResult = factory.post("jumpTrading心跳",
                "/v2/testRequest", null, new TypeReference<JumpTradingApiResultForHeartbeat>() {});
        if (apiResult == null || !apiResult.success()) {
            // 心跳失败， 重新建立连接
            LOGGER.info("jumpTrading-心跳失败/重新建立连接: apiResult={}, 心跳间隔={}",
                    HttpAssets.toJsonString(apiResult), HEART_PERIOD + "秒");
            return;
        }
    }

    /**
     * 注销
     */
    public boolean logout() {
        JumpTradingApiResultBase apiResult = factory.post("jumpTrading注销", "/v2/logout", null,
                new TypeReference<JumpTradingApiResultBase>() {});
        if (apiResult == null) {
            throw new RuntimeException(R.TIME_OUT.toString());
        }
        if (!apiResult.success()) {
            throw new RuntimeException(apiResult.getMsgType());
        }
        return true;
    }

    /**
     * 获取jt支持的交易对
     */
    public List<String> securityDefinitionRequest() {
        JumpTradingApiResultForSecurityRequest apiResult =
                factory.post("jumpTrading支持的交易对", "/v2/securityDefinitionRequest", null,
                        new TypeReference<JumpTradingApiResultForSecurityRequest>() {});
        if (apiResult == null || !apiResult.success()) {
            LOGGER.warn("jumpTrading-获取支持的交易对失败: apiResult={}", HttpAssets.toJsonString(apiResult));
            return null;
        }
        return apiResult.getDefinitions().keySet().stream().map(mapper -> mapper)
                .collect(Collectors.toList());
    }

    /**
     * 订阅或取消订阅交易对
     */
    public JumpTradingApiResultForMarketData marketDataRequest(boolean subscribe, String symbol) {
        String title = subscribe ? "订阅" : "取消订阅";
        JumpTradingApiResultForMarketData apiResult =
                factory.post("jumpTrading" + title + "交易对", "/v2/marketDataRequest",
                        new JTMarketDataParam(symbol).createSubscribeParam(subscribe ? 1 : 2),
                        new TypeReference<JumpTradingApiResultForMarketData>() {});
        if (apiResult == null || !apiResult.success()) {
            LOGGER.warn("jumpTrading" + title + "失败: apiResult={}, symbol={}", apiResult, symbol);
            return null;
        }
        return apiResult;
    }

    /**
     * 查询订单状态
     */
    public JumpTradingApiResultForOrderStatus orderStatus(JTOrderStatusParam orderStatusParam) {
        return this.factory.post("jumpTrading查询订单", "/v2/orderStatus", orderStatusParam,
                new TypeReference<JumpTradingApiResultForOrderStatus>() {});
    }

    /**
     * 下单
     */
    public JumpTradingResultForNewOrderSingle newOrderSingle(
            JumpTradingNewOrderSingleParam newOrderSingleParam) {
        return this.factory.post("jumpTrading下单", "/v2/newOrderSingle", newOrderSingleParam,
                new TypeReference<JumpTradingResultForNewOrderSingle>() {});
    }

}
