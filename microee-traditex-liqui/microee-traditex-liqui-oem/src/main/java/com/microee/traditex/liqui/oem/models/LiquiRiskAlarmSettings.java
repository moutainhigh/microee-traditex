package com.microee.traditex.liqui.oem.models;

import java.io.Serializable;
import java.util.Date;

public class LiquiRiskAlarmSettings implements Serializable {
    private Long id;

    private Long strategyId;

    private String liquiditySymbol;

    private String diskBase;

    private String diskQuote;

    private String exchangeBase;

    private String exchangeQuote;

    private String liquidityWarnDiskBasePos;

    private String liquidityWarnExchangeQuotePos;

    private String liquidityWarnNetworkDelay;

    private String diskWarnDiskBasePos;

    private String diskWarnDiskQuotePos;

    private String diskWarnNetworkDelay;

    private String exchangeWarnExchangeBasePos;

    private String exchangeWarnExchangeQuotePos;

    private String exchangeWarnNetworkDelay;

    private String pnlWarnDayTotal;

    private String pnlWarnWeekTotal;

    private String pnlWarnMonthTotal;

    private String liquidityFuseDiskBasePos;

    private String liquidityFuseExchangeQuotePos;

    private String liquidityFuseNetworkDelay;

    private String diskFuseDiskBasePos;

    private String diskFuseDiskQuotePos;

    private String diskFuseNetworkDelay;

    private String exchangeFuseExchangeBasePos;

    private String exchangeFuseExchangeQuotePos;

    private String exchangeFuseNetworkDelay;

    private String pnlFuseDayTotal;

    private String pnlFuseWeekTotal;

    private String pnlFuseMonthTotal;

    private Date gmtCreated;

    private Date gmtModified;

    private String operatorId;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStrategyId() {
        return strategyId;
    }

    public void setStrategyId(Long strategyId) {
        this.strategyId = strategyId;
    }

    public String getLiquiditySymbol() {
        return liquiditySymbol;
    }

    public void setLiquiditySymbol(String liquiditySymbol) {
        this.liquiditySymbol = liquiditySymbol == null ? null : liquiditySymbol.trim();
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

    public String getLiquidityWarnDiskBasePos() {
        return liquidityWarnDiskBasePos;
    }

    public void setLiquidityWarnDiskBasePos(String liquidityWarnDiskBasePos) {
        this.liquidityWarnDiskBasePos = liquidityWarnDiskBasePos == null ? null : liquidityWarnDiskBasePos.trim();
    }

    public String getLiquidityWarnExchangeQuotePos() {
        return liquidityWarnExchangeQuotePos;
    }

    public void setLiquidityWarnExchangeQuotePos(String liquidityWarnExchangeQuotePos) {
        this.liquidityWarnExchangeQuotePos = liquidityWarnExchangeQuotePos == null ? null : liquidityWarnExchangeQuotePos.trim();
    }

    public String getLiquidityWarnNetworkDelay() {
        return liquidityWarnNetworkDelay;
    }

    public void setLiquidityWarnNetworkDelay(String liquidityWarnNetworkDelay) {
        this.liquidityWarnNetworkDelay = liquidityWarnNetworkDelay == null ? null : liquidityWarnNetworkDelay.trim();
    }

    public String getDiskWarnDiskBasePos() {
        return diskWarnDiskBasePos;
    }

    public void setDiskWarnDiskBasePos(String diskWarnDiskBasePos) {
        this.diskWarnDiskBasePos = diskWarnDiskBasePos == null ? null : diskWarnDiskBasePos.trim();
    }

    public String getDiskWarnDiskQuotePos() {
        return diskWarnDiskQuotePos;
    }

    public void setDiskWarnDiskQuotePos(String diskWarnDiskQuotePos) {
        this.diskWarnDiskQuotePos = diskWarnDiskQuotePos == null ? null : diskWarnDiskQuotePos.trim();
    }

    public String getDiskWarnNetworkDelay() {
        return diskWarnNetworkDelay;
    }

    public void setDiskWarnNetworkDelay(String diskWarnNetworkDelay) {
        this.diskWarnNetworkDelay = diskWarnNetworkDelay == null ? null : diskWarnNetworkDelay.trim();
    }

    public String getExchangeWarnExchangeBasePos() {
        return exchangeWarnExchangeBasePos;
    }

    public void setExchangeWarnExchangeBasePos(String exchangeWarnExchangeBasePos) {
        this.exchangeWarnExchangeBasePos = exchangeWarnExchangeBasePos == null ? null : exchangeWarnExchangeBasePos.trim();
    }

    public String getExchangeWarnExchangeQuotePos() {
        return exchangeWarnExchangeQuotePos;
    }

    public void setExchangeWarnExchangeQuotePos(String exchangeWarnExchangeQuotePos) {
        this.exchangeWarnExchangeQuotePos = exchangeWarnExchangeQuotePos == null ? null : exchangeWarnExchangeQuotePos.trim();
    }

    public String getExchangeWarnNetworkDelay() {
        return exchangeWarnNetworkDelay;
    }

    public void setExchangeWarnNetworkDelay(String exchangeWarnNetworkDelay) {
        this.exchangeWarnNetworkDelay = exchangeWarnNetworkDelay == null ? null : exchangeWarnNetworkDelay.trim();
    }

    public String getPnlWarnDayTotal() {
        return pnlWarnDayTotal;
    }

    public void setPnlWarnDayTotal(String pnlWarnDayTotal) {
        this.pnlWarnDayTotal = pnlWarnDayTotal == null ? null : pnlWarnDayTotal.trim();
    }

    public String getPnlWarnWeekTotal() {
        return pnlWarnWeekTotal;
    }

    public void setPnlWarnWeekTotal(String pnlWarnWeekTotal) {
        this.pnlWarnWeekTotal = pnlWarnWeekTotal == null ? null : pnlWarnWeekTotal.trim();
    }

    public String getPnlWarnMonthTotal() {
        return pnlWarnMonthTotal;
    }

    public void setPnlWarnMonthTotal(String pnlWarnMonthTotal) {
        this.pnlWarnMonthTotal = pnlWarnMonthTotal == null ? null : pnlWarnMonthTotal.trim();
    }

    public String getLiquidityFuseDiskBasePos() {
        return liquidityFuseDiskBasePos;
    }

    public void setLiquidityFuseDiskBasePos(String liquidityFuseDiskBasePos) {
        this.liquidityFuseDiskBasePos = liquidityFuseDiskBasePos == null ? null : liquidityFuseDiskBasePos.trim();
    }

    public String getLiquidityFuseExchangeQuotePos() {
        return liquidityFuseExchangeQuotePos;
    }

    public void setLiquidityFuseExchangeQuotePos(String liquidityFuseExchangeQuotePos) {
        this.liquidityFuseExchangeQuotePos = liquidityFuseExchangeQuotePos == null ? null : liquidityFuseExchangeQuotePos.trim();
    }

    public String getLiquidityFuseNetworkDelay() {
        return liquidityFuseNetworkDelay;
    }

    public void setLiquidityFuseNetworkDelay(String liquidityFuseNetworkDelay) {
        this.liquidityFuseNetworkDelay = liquidityFuseNetworkDelay == null ? null : liquidityFuseNetworkDelay.trim();
    }

    public String getDiskFuseDiskBasePos() {
        return diskFuseDiskBasePos;
    }

    public void setDiskFuseDiskBasePos(String diskFuseDiskBasePos) {
        this.diskFuseDiskBasePos = diskFuseDiskBasePos == null ? null : diskFuseDiskBasePos.trim();
    }

    public String getDiskFuseDiskQuotePos() {
        return diskFuseDiskQuotePos;
    }

    public void setDiskFuseDiskQuotePos(String diskFuseDiskQuotePos) {
        this.diskFuseDiskQuotePos = diskFuseDiskQuotePos == null ? null : diskFuseDiskQuotePos.trim();
    }

    public String getDiskFuseNetworkDelay() {
        return diskFuseNetworkDelay;
    }

    public void setDiskFuseNetworkDelay(String diskFuseNetworkDelay) {
        this.diskFuseNetworkDelay = diskFuseNetworkDelay == null ? null : diskFuseNetworkDelay.trim();
    }

    public String getExchangeFuseExchangeBasePos() {
        return exchangeFuseExchangeBasePos;
    }

    public void setExchangeFuseExchangeBasePos(String exchangeFuseExchangeBasePos) {
        this.exchangeFuseExchangeBasePos = exchangeFuseExchangeBasePos == null ? null : exchangeFuseExchangeBasePos.trim();
    }

    public String getExchangeFuseExchangeQuotePos() {
        return exchangeFuseExchangeQuotePos;
    }

    public void setExchangeFuseExchangeQuotePos(String exchangeFuseExchangeQuotePos) {
        this.exchangeFuseExchangeQuotePos = exchangeFuseExchangeQuotePos == null ? null : exchangeFuseExchangeQuotePos.trim();
    }

    public String getExchangeFuseNetworkDelay() {
        return exchangeFuseNetworkDelay;
    }

    public void setExchangeFuseNetworkDelay(String exchangeFuseNetworkDelay) {
        this.exchangeFuseNetworkDelay = exchangeFuseNetworkDelay == null ? null : exchangeFuseNetworkDelay.trim();
    }

    public String getPnlFuseDayTotal() {
        return pnlFuseDayTotal;
    }

    public void setPnlFuseDayTotal(String pnlFuseDayTotal) {
        this.pnlFuseDayTotal = pnlFuseDayTotal == null ? null : pnlFuseDayTotal.trim();
    }

    public String getPnlFuseWeekTotal() {
        return pnlFuseWeekTotal;
    }

    public void setPnlFuseWeekTotal(String pnlFuseWeekTotal) {
        this.pnlFuseWeekTotal = pnlFuseWeekTotal == null ? null : pnlFuseWeekTotal.trim();
    }

    public String getPnlFuseMonthTotal() {
        return pnlFuseMonthTotal;
    }

    public void setPnlFuseMonthTotal(String pnlFuseMonthTotal) {
        this.pnlFuseMonthTotal = pnlFuseMonthTotal == null ? null : pnlFuseMonthTotal.trim();
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

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId == null ? null : operatorId.trim();
    }
}