package com.microee.traditex.archive.oem.models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class SolrOrderDetail implements Serializable {
    private String solrDetailId;

    private String solrClientOrderId;

    private String solrResultOrderId;

    private String vender;

    private String solrAccount;

    private String solrOrderSymbol;

    private BigDecimal solrFilledAmount;

    private BigDecimal solrFilledPrice;

    private BigDecimal filledCashAmount;

    private String solrOrderState;

    private String solrOrderType;

    private BigDecimal unfilledAmount;

    private Date emitterTime;

    private Date createdAt;

    private static final long serialVersionUID = 1L;

    public String getSolrDetailId() {
        return solrDetailId;
    }

    public void setSolrDetailId(String solrDetailId) {
        this.solrDetailId = solrDetailId == null ? null : solrDetailId.trim();
    }

    public String getSolrClientOrderId() {
        return solrClientOrderId;
    }

    public void setSolrClientOrderId(String solrClientOrderId) {
        this.solrClientOrderId = solrClientOrderId == null ? null : solrClientOrderId.trim();
    }

    public String getSolrResultOrderId() {
        return solrResultOrderId;
    }

    public void setSolrResultOrderId(String solrResultOrderId) {
        this.solrResultOrderId = solrResultOrderId == null ? null : solrResultOrderId.trim();
    }

    public String getVender() {
        return vender;
    }

    public void setVender(String vender) {
        this.vender = vender == null ? null : vender.trim();
    }

    public String getSolrAccount() {
        return solrAccount;
    }

    public void setSolrAccount(String solrAccount) {
        this.solrAccount = solrAccount == null ? null : solrAccount.trim();
    }

    public String getSolrOrderSymbol() {
        return solrOrderSymbol;
    }

    public void setSolrOrderSymbol(String solrOrderSymbol) {
        this.solrOrderSymbol = solrOrderSymbol == null ? null : solrOrderSymbol.trim();
    }

    public BigDecimal getSolrFilledAmount() {
        return solrFilledAmount;
    }

    public void setSolrFilledAmount(BigDecimal solrFilledAmount) {
        this.solrFilledAmount = solrFilledAmount;
    }

    public BigDecimal getSolrFilledPrice() {
        return solrFilledPrice;
    }

    public void setSolrFilledPrice(BigDecimal solrFilledPrice) {
        this.solrFilledPrice = solrFilledPrice;
    }

    public BigDecimal getFilledCashAmount() {
        return filledCashAmount;
    }

    public void setFilledCashAmount(BigDecimal filledCashAmount) {
        this.filledCashAmount = filledCashAmount;
    }

    public String getSolrOrderState() {
        return solrOrderState;
    }

    public void setSolrOrderState(String solrOrderState) {
        this.solrOrderState = solrOrderState == null ? null : solrOrderState.trim();
    }

    public String getSolrOrderType() {
        return solrOrderType;
    }

    public void setSolrOrderType(String solrOrderType) {
        this.solrOrderType = solrOrderType == null ? null : solrOrderType.trim();
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