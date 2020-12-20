package com.microee.traditex.inbox.oem.jumptrading.apiparam;

import java.util.Arrays;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * jt /v2/marketDataRequest 请求参数
 * 
 * @author zhangchunhui
 */
public class JTMarketDataParam {

    @JsonProperty("SubscriptionRequestType")
    private String subscriptionRequestType; // "1"=订阅, "2"=取消订阅
    private List<String> symbols; // 希望订阅或者取消订阅的交易对

    public JTMarketDataParam(String... symbol) {
        this.symbols = Arrays.asList(symbol);
    }

    public JTMarketDataParam(List<String> symbol) {
        this.symbols = symbol;
    }

    public String getSubscriptionRequestType() {
        return subscriptionRequestType;
    }

    public void setSubscriptionRequestType(String subscriptionRequestType) {
        this.subscriptionRequestType = subscriptionRequestType;
    }

    public List<String> getSymbols() {
        return symbols;
    }

    public void setSymbols(List<String> symbols) {
        this.symbols = symbols;
    }

    /**
     * 订阅交易对
     * 
     * @return
     */
    public JTMarketDataParam createSubscribeParam(int oneOrTwo) {
        this.setSubscriptionRequestType(oneOrTwo == 1 ? "1" : "2");
        return this;
    }

    /**
     * 订阅交易对
     * 
     * @return
     */
    public JTMarketDataParam createSubscribeParam() {
        this.setSubscriptionRequestType("1");
        return this;
    }

    /**
     * 取消订阅交易对
     * 
     * @return
     */
    public JTMarketDataParam createUnSubscribeParam() {
        this.setSubscriptionRequestType("2");
        return this;
    }

}
