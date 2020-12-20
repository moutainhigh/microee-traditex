package com.microee.traditex.inbox.oem.hbitex.vo;


import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonProperty;

public class HBiTexOrderDetails implements Serializable {

    private static final long serialVersionUID = -3002603004125378880L;

    @JsonProperty("id")
    private String id; // ": 51080391524355,
    @JsonProperty("symbol")
    private String symbol; // ": "htjpy",
    @JsonProperty("account-id")
    private String accountId; // ": 111214941,
    @JsonProperty("client-order-id")
    private String clientOrderId; // ": "1234572752",
    @JsonProperty("amount")
    private String amount; // ": "1.000000000000000000",
    @JsonProperty("price")
    private String price; // ": "457.992000000000000000",
    @JsonProperty("created-at")
    private Long createdAt; // ": 1592396572290,
    @JsonProperty("type")
    private String type; // ": "buy-limit-fok",
    @JsonProperty("field-amount")
    private String fieldAmount; // ": "1.000000000000000000",
    @JsonProperty("field-cash-amount")
    private String fieldCashAmount; // ": "449.000000000000000000",
    @JsonProperty("field-fees")
    private String fieldFees; // ": "0.002000000000000000",
    @JsonProperty("finished-at")
    private Long finishedAt; // ": 1592396572502,
    @JsonProperty("source")
    private String source; // ": "spot-api",
    @JsonProperty("state")
    private String state; // ": "filled",
    @JsonProperty("canceled-at")
    private Long canceledAt; // ": 0

    public HBiTexOrderDetails() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getClientOrderId() {
        return clientOrderId;
    }

    public void setClientOrderId(String clientOrderId) {
        this.clientOrderId = clientOrderId;
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

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFieldAmount() {
        return fieldAmount;
    }

    public void setFieldAmount(String fieldAmount) {
        this.fieldAmount = fieldAmount;
    }

    public String getFieldCashAmount() {
        return fieldCashAmount;
    }

    public void setFieldCashAmount(String fieldCashAmount) {
        this.fieldCashAmount = fieldCashAmount;
    }

    public String getFieldFees() {
        return fieldFees;
    }

    public void setFieldFees(String fieldFees) {
        this.fieldFees = fieldFees;
    }

    public Long getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(Long finishedAt) {
        this.finishedAt = finishedAt;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Long getCanceledAt() {
        return canceledAt;
    }

    public void setCanceledAt(Long canceledAt) {
        this.canceledAt = canceledAt;
    }

}