package com.microee.traditex.inbox.oem.hbitex.po;

import java.io.Serializable;
import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonProperty;

public class HBiTexOrderPlaceParam implements Serializable {

    private static final long serialVersionUID = -6351203423413670606L;

    @JsonProperty("account-id")
    private String accountId;
    private String symbol; // btcusdt, ethbtc
    private String type; // 订单类型，包括, buy-limit-fok, sell-limit-fok, 方向一致
    private String amount;
    private String price;
    private String source; // 现货交易填写“spot-api”，逐仓杠杆交易填写“margin-api”，全仓杠杆交易填写“super-margin-api”
    @JsonProperty("client-order-id")
    private String clientOrderId;
    
    public HBiTexOrderPlaceParam(String symbol, String side, BigDecimal amount, BigDecimal price, String clientOrderId) {
        this.symbol = symbol == null ? "" : symbol.toLowerCase();
        this.type = side == null || side.equals("BUY") ? "buy-limit-fok" : "sell-limit-fok";
        this.amount = amount == null ? "0" : amount.toString();
        this.price = price == null ? "0" : price.toString();
        this.source = "spot-api";
        this.clientOrderId = clientOrderId;
    }
    
    public HBiTexOrderPlaceParam(String symbol, String side, BigDecimal amount, BigDecimal price, String clientOrderId, String type) {
        this.symbol = symbol == null ? "" : symbol.toLowerCase();
        this.type = type;
        this.amount = amount == null ? "0" : amount.toString();
        this.price = price == null ? "0" : price.toString();
        this.source = "spot-api";
        this.clientOrderId = clientOrderId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getClientOrderId() {
        return clientOrderId;
    }

    public void setClientOrderId(String clientOrderId) {
        this.clientOrderId = clientOrderId;
    }

}