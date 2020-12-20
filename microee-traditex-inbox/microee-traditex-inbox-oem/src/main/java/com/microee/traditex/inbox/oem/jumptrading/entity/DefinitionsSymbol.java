package com.microee.traditex.inbox.oem.jumptrading.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DefinitionsSymbol implements Serializable {

    private static final long serialVersionUID = 9125107303082425294L;

    @JsonProperty("BeginString")
    private String beginString;
    @JsonProperty("MsgType")
    private String msgType;
    @JsonProperty("MsgSeqNum")
    private Long msgSeqNum;
    @JsonProperty("SendingTime")
    private String sendingTime;
    @JsonProperty("Symbol")
    private String symbol;
    @JsonProperty("SecurityType")
    private String securityType;
    @JsonProperty("MinPriceIncrement")
    private BigDecimal minPriceIncrement;
    @JsonProperty("TotNumReports")
    private Integer totNumReports;
    @JsonProperty("RoundLot")
    private BigDecimal roundLot;
    @JsonProperty("Currency")
    private String currency;
    @JsonProperty("ext")
    private DefinitionsSymbolExt ext;
    
    public DefinitionsSymbol() {
        
    }

    public String getBeginString() {
        return beginString;
    }

    public void setBeginString(String beginString) {
        this.beginString = beginString;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public Long getMsgSeqNum() {
        return msgSeqNum;
    }

    public void setMsgSeqNum(Long msgSeqNum) {
        this.msgSeqNum = msgSeqNum;
    }

    public String getSendingTime() {
        return sendingTime;
    }

    public void setSendingTime(String sendingTime) {
        this.sendingTime = sendingTime;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getSecurityType() {
        return securityType;
    }

    public void setSecurityType(String securityType) {
        this.securityType = securityType;
    }

    public BigDecimal getMinPriceIncrement() {
        return minPriceIncrement;
    }

    public void setMinPriceIncrement(BigDecimal minPriceIncrement) {
        this.minPriceIncrement = minPriceIncrement;
    }

    public Integer getTotNumReports() {
        return totNumReports;
    }

    public void setTotNumReports(Integer totNumReports) {
        this.totNumReports = totNumReports;
    }

    public BigDecimal getRoundLot() {
        return roundLot;
    }

    public void setRoundLot(BigDecimal roundLot) {
        this.roundLot = roundLot;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public DefinitionsSymbolExt getExt() {
        return ext;
    }

    public void setExt(DefinitionsSymbolExt ext) {
        this.ext = ext;
    }

}