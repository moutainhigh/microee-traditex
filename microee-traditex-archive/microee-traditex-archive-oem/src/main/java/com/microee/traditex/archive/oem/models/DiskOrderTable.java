package com.microee.traditex.archive.oem.models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class DiskOrderTable implements Serializable {
    private String diskClientOrderId;

    private String diskResultOrderId;

    private String orderBookId;

    private String vender;

    private String targetSymbol;

    private String targetSide;

    private BigDecimal targetAmount;

    private BigDecimal targetPrice;

    private BigDecimal usdtPrice;

    private BigDecimal usdCurrencyPricing;

    private BigDecimal usdtUsdRate;

    private BigDecimal diskAmount;

    private BigDecimal diskPrice;

    private Integer diskPricePrec;

    private Integer diskAmountPrec;

    private String diskOrderType;

    private String diskAccount;

    private String diskOrderResult;

    private Integer diskOcst;

    private Date createdAt;

    private static final long serialVersionUID = 1L;

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

    public String getOrderBookId() {
        return orderBookId;
    }

    public void setOrderBookId(String orderBookId) {
        this.orderBookId = orderBookId == null ? null : orderBookId.trim();
    }

    public String getVender() {
        return vender;
    }

    public void setVender(String vender) {
        this.vender = vender == null ? null : vender.trim();
    }

    public String getTargetSymbol() {
        return targetSymbol;
    }

    public void setTargetSymbol(String targetSymbol) {
        this.targetSymbol = targetSymbol == null ? null : targetSymbol.trim();
    }

    public String getTargetSide() {
        return targetSide;
    }

    public void setTargetSide(String targetSide) {
        this.targetSide = targetSide == null ? null : targetSide.trim();
    }

    public BigDecimal getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(BigDecimal targetAmount) {
        this.targetAmount = targetAmount;
    }

    public BigDecimal getTargetPrice() {
        return targetPrice;
    }

    public void setTargetPrice(BigDecimal targetPrice) {
        this.targetPrice = targetPrice;
    }

    public BigDecimal getUsdtPrice() {
        return usdtPrice;
    }

    public void setUsdtPrice(BigDecimal usdtPrice) {
        this.usdtPrice = usdtPrice;
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

    public BigDecimal getDiskAmount() {
        return diskAmount;
    }

    public void setDiskAmount(BigDecimal diskAmount) {
        this.diskAmount = diskAmount;
    }

    public BigDecimal getDiskPrice() {
        return diskPrice;
    }

    public void setDiskPrice(BigDecimal diskPrice) {
        this.diskPrice = diskPrice;
    }

    public Integer getDiskPricePrec() {
        return diskPricePrec;
    }

    public void setDiskPricePrec(Integer diskPricePrec) {
        this.diskPricePrec = diskPricePrec;
    }

    public Integer getDiskAmountPrec() {
        return diskAmountPrec;
    }

    public void setDiskAmountPrec(Integer diskAmountPrec) {
        this.diskAmountPrec = diskAmountPrec;
    }

    public String getDiskOrderType() {
        return diskOrderType;
    }

    public void setDiskOrderType(String diskOrderType) {
        this.diskOrderType = diskOrderType == null ? null : diskOrderType.trim();
    }

    public String getDiskAccount() {
        return diskAccount;
    }

    public void setDiskAccount(String diskAccount) {
        this.diskAccount = diskAccount == null ? null : diskAccount.trim();
    }

    public String getDiskOrderResult() {
        return diskOrderResult;
    }

    public void setDiskOrderResult(String diskOrderResult) {
        this.diskOrderResult = diskOrderResult == null ? null : diskOrderResult.trim();
    }

    public Integer getDiskOcst() {
        return diskOcst;
    }

    public void setDiskOcst(Integer diskOcst) {
        this.diskOcst = diskOcst;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}