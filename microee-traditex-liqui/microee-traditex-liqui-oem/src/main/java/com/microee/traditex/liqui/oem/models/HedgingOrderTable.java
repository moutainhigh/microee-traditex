package com.microee.traditex.liqui.oem.models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class HedgingOrderTable implements Serializable {
    private String orderId;

    private String diskClientOrderId;

    private String diskMatchId;

    private String diskFilledAmount;

    private Long diskTradeTime;

    private String side;

    private BigDecimal beforeBalance;

    private String instrument;

    private BigDecimal pricing;

    private BigDecimal unit;

    private Date createdAt;

    private String createOrderResult;

    private static final long serialVersionUID = 1L;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId == null ? null : orderId.trim();
    }

    public String getDiskClientOrderId() {
        return diskClientOrderId;
    }

    public void setDiskClientOrderId(String diskClientOrderId) {
        this.diskClientOrderId = diskClientOrderId == null ? null : diskClientOrderId.trim();
    }

    public String getDiskMatchId() {
        return diskMatchId;
    }

    public void setDiskMatchId(String diskMatchId) {
        this.diskMatchId = diskMatchId == null ? null : diskMatchId.trim();
    }

    public String getDiskFilledAmount() {
        return diskFilledAmount;
    }

    public void setDiskFilledAmount(String diskFilledAmount) {
        this.diskFilledAmount = diskFilledAmount == null ? null : diskFilledAmount.trim();
    }

    public Long getDiskTradeTime() {
        return diskTradeTime;
    }

    public void setDiskTradeTime(Long diskTradeTime) {
        this.diskTradeTime = diskTradeTime;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side == null ? null : side.trim();
    }

    public BigDecimal getBeforeBalance() {
        return beforeBalance;
    }

    public void setBeforeBalance(BigDecimal beforeBalance) {
        this.beforeBalance = beforeBalance;
    }

    public String getInstrument() {
        return instrument;
    }

    public void setInstrument(String instrument) {
        this.instrument = instrument == null ? null : instrument.trim();
    }

    public BigDecimal getPricing() {
        return pricing;
    }

    public void setPricing(BigDecimal pricing) {
        this.pricing = pricing;
    }

    public BigDecimal getUnit() {
        return unit;
    }

    public void setUnit(BigDecimal unit) {
        this.unit = unit;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreateOrderResult() {
        return createOrderResult;
    }

    public void setCreateOrderResult(String createOrderResult) {
        this.createOrderResult = createOrderResult == null ? null : createOrderResult.trim();
    }
}