package com.microee.traditex.archive.oem.models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class SolrOrderTable implements Serializable {
    private String solrClientOrderId;

    private String solrResultOrderId;

    private String diskResultOrderId;

    private String diskSymbol;

    private String orderBookId;

    private Integer solrOsst;

    private String vender;

    private String solrAccount;

    private String solrSymbol;

    private String solrSide;

    private BigDecimal solrAmount;

    private BigDecimal solrPrice;

    private Integer solrAmountPrec;

    private Integer solrPricePrec;

    private BigDecimal solrOrderAmount;

    private BigDecimal solrOrderPrice;

    private String solrOrderType;

    private BigDecimal usdCurrencyPricing;

    private BigDecimal usdtUsdRate;

    private Date createdAt;

    private static final long serialVersionUID = 1L;

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

    public String getDiskResultOrderId() {
        return diskResultOrderId;
    }

    public void setDiskResultOrderId(String diskResultOrderId) {
        this.diskResultOrderId = diskResultOrderId == null ? null : diskResultOrderId.trim();
    }

    public String getDiskSymbol() {
        return diskSymbol;
    }

    public void setDiskSymbol(String diskSymbol) {
        this.diskSymbol = diskSymbol == null ? null : diskSymbol.trim();
    }

    public String getOrderBookId() {
        return orderBookId;
    }

    public void setOrderBookId(String orderBookId) {
        this.orderBookId = orderBookId == null ? null : orderBookId.trim();
    }

    public Integer getSolrOsst() {
        return solrOsst;
    }

    public void setSolrOsst(Integer solrOsst) {
        this.solrOsst = solrOsst;
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

    public String getSolrSymbol() {
        return solrSymbol;
    }

    public void setSolrSymbol(String solrSymbol) {
        this.solrSymbol = solrSymbol == null ? null : solrSymbol.trim();
    }

    public String getSolrSide() {
        return solrSide;
    }

    public void setSolrSide(String solrSide) {
        this.solrSide = solrSide == null ? null : solrSide.trim();
    }

    public BigDecimal getSolrAmount() {
        return solrAmount;
    }

    public void setSolrAmount(BigDecimal solrAmount) {
        this.solrAmount = solrAmount;
    }

    public BigDecimal getSolrPrice() {
        return solrPrice;
    }

    public void setSolrPrice(BigDecimal solrPrice) {
        this.solrPrice = solrPrice;
    }

    public Integer getSolrAmountPrec() {
        return solrAmountPrec;
    }

    public void setSolrAmountPrec(Integer solrAmountPrec) {
        this.solrAmountPrec = solrAmountPrec;
    }

    public Integer getSolrPricePrec() {
        return solrPricePrec;
    }

    public void setSolrPricePrec(Integer solrPricePrec) {
        this.solrPricePrec = solrPricePrec;
    }

    public BigDecimal getSolrOrderAmount() {
        return solrOrderAmount;
    }

    public void setSolrOrderAmount(BigDecimal solrOrderAmount) {
        this.solrOrderAmount = solrOrderAmount;
    }

    public BigDecimal getSolrOrderPrice() {
        return solrOrderPrice;
    }

    public void setSolrOrderPrice(BigDecimal solrOrderPrice) {
        this.solrOrderPrice = solrOrderPrice;
    }

    public String getSolrOrderType() {
        return solrOrderType;
    }

    public void setSolrOrderType(String solrOrderType) {
        this.solrOrderType = solrOrderType == null ? null : solrOrderType.trim();
    }

    public BigDecimal getUsdCurrencyPricing() {
        return usdCurrencyPricing;
    }

    public void setUsdCurrencyPricing(BigDecimal usdCurrencyPricing) {
        this.usdCurrencyPricing = usdCurrencyPricing;
    }

    public BigDecimal getUsdtUsdRate() {
        return usdtUsdRate;
    }

    public void setUsdtUsdRate(BigDecimal usdtUsdRate) {
        this.usdtUsdRate = usdtUsdRate;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}