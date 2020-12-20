package com.microee.traditex.liqui.oem.models;

import java.io.Serializable;
import java.util.Date;

public class LiquiRiskStrategySettings implements Serializable {
    private Long id;

    private String liquiditySymbol;

    private String diskHedgeSymbol;

    private String exchangeHedgeSymbol;

    private String diskBase;

    private String diskQuote;

    private String exchangeBase;

    private String exchangeQuote;

    private Integer buyState;

    private Integer sellState;

    private Integer depthStep;

    private String buyMoneyLimit;

    private String sellMoneyLimit;

    private Integer pricePrecson;

    private Integer quantityPrecson;

    private Integer diskBuyPrecson;

    private Integer diskSellPrecson;

    private String diskPriceScaleMax;

    private String diskPriceScaleMin;

    private Integer exchangeBuyPricePrecson;

    private Integer exchangeSellPricePrecson;

    private String exchangeConfig;

    private Date gmtCreated;

    private Date gmtModified;

    private Integer operatorId;

    private String buyPriceIncrRate;

    private String sellPriceIncrRate;

    private static final long serialVersionUID = 1L;

    // 是否处于熔断(盘口关闭阶段)
    public boolean isFuse() {
        return !sellState.equals(1) && !buyState.equals(1);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLiquiditySymbol() {
        return liquiditySymbol;
    }

    public void setLiquiditySymbol(String liquiditySymbol) {
        this.liquiditySymbol = liquiditySymbol == null ? null : liquiditySymbol.trim();
    }

    public String getDiskHedgeSymbol() {
        return diskHedgeSymbol;
    }

    public void setDiskHedgeSymbol(String diskHedgeSymbol) {
        this.diskHedgeSymbol = diskHedgeSymbol == null ? null : diskHedgeSymbol.trim();
    }

    public String getExchangeHedgeSymbol() {
        return exchangeHedgeSymbol;
    }

    public void setExchangeHedgeSymbol(String exchangeHedgeSymbol) {
        this.exchangeHedgeSymbol = exchangeHedgeSymbol == null ? null : exchangeHedgeSymbol.trim();
    }

    public String getDiskBase() {
        return diskBase;
    }

    public void setDiskBase(String diskBase) {
        this.diskBase = diskBase == null ? null : diskBase.trim();
    }

    public String getDiskQuote() {
        return diskQuote;
    }

    public void setDiskQuote(String diskQuote) {
        this.diskQuote = diskQuote == null ? null : diskQuote.trim();
    }

    public String getExchangeBase() {
        return exchangeBase;
    }

    public void setExchangeBase(String exchangeBase) {
        this.exchangeBase = exchangeBase == null ? null : exchangeBase.trim();
    }

    public String getExchangeQuote() {
        return exchangeQuote;
    }

    public void setExchangeQuote(String exchangeQuote) {
        this.exchangeQuote = exchangeQuote == null ? null : exchangeQuote.trim();
    }

    public Integer getBuyState() {
        return buyState;
    }

    public void setBuyState(Integer buyState) {
        this.buyState = buyState;
    }

    public Integer getSellState() {
        return sellState;
    }

    public void setSellState(Integer sellState) {
        this.sellState = sellState;
    }

    public Integer getDepthStep() {
        return depthStep;
    }

    public void setDepthStep(Integer depthStep) {
        this.depthStep = depthStep;
    }

    public String getBuyMoneyLimit() {
        return buyMoneyLimit;
    }

    public void setBuyMoneyLimit(String buyMoneyLimit) {
        this.buyMoneyLimit = buyMoneyLimit == null ? null : buyMoneyLimit.trim();
    }

    public String getSellMoneyLimit() {
        return sellMoneyLimit;
    }

    public void setSellMoneyLimit(String sellMoneyLimit) {
        this.sellMoneyLimit = sellMoneyLimit == null ? null : sellMoneyLimit.trim();
    }

    public Integer getPricePrecson() {
        return pricePrecson;
    }

    public void setPricePrecson(Integer pricePrecson) {
        this.pricePrecson = pricePrecson;
    }

    public Integer getQuantityPrecson() {
        return quantityPrecson;
    }

    public void setQuantityPrecson(Integer quantityPrecson) {
        this.quantityPrecson = quantityPrecson;
    }

    public Integer getDiskBuyPrecson() {
        return diskBuyPrecson;
    }

    public void setDiskBuyPrecson(Integer diskBuyPrecson) {
        this.diskBuyPrecson = diskBuyPrecson;
    }

    public Integer getDiskSellPrecson() {
        return diskSellPrecson;
    }

    public void setDiskSellPrecson(Integer diskSellPrecson) {
        this.diskSellPrecson = diskSellPrecson;
    }

    public String getDiskPriceScaleMax() {
        return diskPriceScaleMax;
    }

    public void setDiskPriceScaleMax(String diskPriceScaleMax) {
        this.diskPriceScaleMax = diskPriceScaleMax == null ? null : diskPriceScaleMax.trim();
    }

    public String getDiskPriceScaleMin() {
        return diskPriceScaleMin;
    }

    public void setDiskPriceScaleMin(String diskPriceScaleMin) {
        this.diskPriceScaleMin = diskPriceScaleMin == null ? null : diskPriceScaleMin.trim();
    }

    public Integer getExchangeBuyPricePrecson() {
        return exchangeBuyPricePrecson;
    }

    public void setExchangeBuyPricePrecson(Integer exchangeBuyPricePrecson) {
        this.exchangeBuyPricePrecson = exchangeBuyPricePrecson;
    }

    public Integer getExchangeSellPricePrecson() {
        return exchangeSellPricePrecson;
    }

    public void setExchangeSellPricePrecson(Integer exchangeSellPricePrecson) {
        this.exchangeSellPricePrecson = exchangeSellPricePrecson;
    }

    public String getExchangeConfig() {
        return exchangeConfig;
    }

    public void setExchangeConfig(String exchangeConfig) {
        this.exchangeConfig = exchangeConfig == null ? null : exchangeConfig.trim();
    }

    public Date getGmtCreated() {
        return gmtCreated;
    }

    public void setGmtCreated(Date gmtCreated) {
        this.gmtCreated = gmtCreated;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    public String getBuyPriceIncrRate() {
        return buyPriceIncrRate;
    }

    public void setBuyPriceIncrRate(String buyPriceIncrRate) {
        this.buyPriceIncrRate = buyPriceIncrRate == null ? null : buyPriceIncrRate.trim();
    }

    public String getSellPriceIncrRate() {
        return sellPriceIncrRate;
    }

    public void setSellPriceIncrRate(String sellPriceIncrRate) {
        this.sellPriceIncrRate = sellPriceIncrRate == null ? null : sellPriceIncrRate.trim();
    }
}