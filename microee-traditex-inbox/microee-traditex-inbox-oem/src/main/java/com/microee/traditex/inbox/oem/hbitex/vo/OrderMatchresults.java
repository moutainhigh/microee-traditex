package com.microee.traditex.inbox.oem.hbitex.vo;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonProperty;

public class OrderMatchresults implements Serializable {

    private static final long serialVersionUID = -4489352380237100120L;

    private String id; // 29553,
    @JsonProperty("order-id")
    private String orderId; // 59378,
    @JsonProperty("match-id")
    private String matchId; // 59335,
    @JsonProperty("trade-id")
    private String tradeId; // 100282808529,
    private String symbol; // "ethusdt",
    private String type; // "buy-limit",
    private String source; // "api",
    private String price; // "100.1000000000",
    @JsonProperty("filled-amount")
    private String filledAmount; // "9.1155000000",
    @JsonProperty("filled-fees")
    private String filledFees; // "0.0182310000",
    @JsonProperty("created-at")
    private Long createdAt; // 1494901400435,
    private String role; // "maker",
    @JsonProperty("filled-points")
    private String filledPoints; // "0.0",
    @JsonProperty("fee-deduct-currency")
    private String feeDeductCurrency; // ""

    public OrderMatchresults() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
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

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getFilledAmount() {
        return filledAmount;
    }

    public void setFilledAmount(String filledAmount) {
        this.filledAmount = filledAmount;
    }

    public String getFilledFees() {
        return filledFees;
    }

    public void setFilledFees(String filledFees) {
        this.filledFees = filledFees;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getFilledPoints() {
        return filledPoints;
    }

    public void setFilledPoints(String filledPoints) {
        this.filledPoints = filledPoints;
    }

    public String getFeeDeductCurrency() {
        return feeDeductCurrency;
    }

    public void setFeeDeductCurrency(String feeDeductCurrency) {
        this.feeDeductCurrency = feeDeductCurrency;
    }

}
