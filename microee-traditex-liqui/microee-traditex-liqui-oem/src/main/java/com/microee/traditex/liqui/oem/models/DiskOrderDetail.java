package com.microee.traditex.liqui.oem.models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class DiskOrderDetail implements Serializable {
    private String diskDetailId;

    private String diskClientOrderId;

    private String diskResultOrderId;

    private String matchId;

    private String vender;

    private String diskOrderSymbol;

    private BigDecimal diskFilledAmount;

    private BigDecimal diskFilledPrice;

    private BigDecimal filledCashAmount;

    private String diskOrderState;

    private String diskOrderType;

    private BigDecimal unfilledAmount;

    private Date emitterTime;

    private Date createdAt;

    private static final long serialVersionUID = 1L;

    public String getDiskDetailId() {
        return diskDetailId;
    }

    public void setDiskDetailId(String diskDetailId) {
        this.diskDetailId = diskDetailId == null ? null : diskDetailId.trim();
    }

    public String getDiskClientOrderId() {
        return diskClientOrderId;
    }

    public void setDiskClientOrderId(String diskClientOrderId) {
        this.diskClientOrderId = diskClientOrderId == null ? null : diskClientOrderId.trim();
    }

    public String getDiskResultOrderId() {
        return diskResultOrderId;
    }

    public void setDiskResultOrderId(String diskResultOrderId) {
        this.diskResultOrderId = diskResultOrderId == null ? null : diskResultOrderId.trim();
    }

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId == null ? null : matchId.trim();
    }

    public String getVender() {
        return vender;
    }

    public void setVender(String vender) {
        this.vender = vender == null ? null : vender.trim();
    }

    public String getDiskOrderSymbol() {
        return diskOrderSymbol;
    }

    public void setDiskOrderSymbol(String diskOrderSymbol) {
        this.diskOrderSymbol = diskOrderSymbol == null ? null : diskOrderSymbol.trim();
    }

    public BigDecimal getDiskFilledAmount() {
        return diskFilledAmount;
    }

    public void setDiskFilledAmount(BigDecimal diskFilledAmount) {
        this.diskFilledAmount = diskFilledAmount;
    }

    public BigDecimal getDiskFilledPrice() {
        return diskFilledPrice;
    }

    public void setDiskFilledPrice(BigDecimal diskFilledPrice) {
        this.diskFilledPrice = diskFilledPrice;
    }

    public BigDecimal getFilledCashAmount() {
        return filledCashAmount;
    }

    public void setFilledCashAmount(BigDecimal filledCashAmount) {
        this.filledCashAmount = filledCashAmount;
    }

    public String getDiskOrderState() {
        return diskOrderState;
    }

    public void setDiskOrderState(String diskOrderState) {
        this.diskOrderState = diskOrderState == null ? null : diskOrderState.trim();
    }

    public String getDiskOrderType() {
        return diskOrderType;
    }

    public void setDiskOrderType(String diskOrderType) {
        this.diskOrderType = diskOrderType == null ? null : diskOrderType.trim();
    }

    public BigDecimal getUnfilledAmount() {
        return unfilledAmount;
    }

    public void setUnfilledAmount(BigDecimal unfilledAmount) {
        this.unfilledAmount = unfilledAmount;
    }

    public Date getEmitterTime() {
        return emitterTime;
    }

    public void setEmitterTime(Date emitterTime) {
        this.emitterTime = emitterTime;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}