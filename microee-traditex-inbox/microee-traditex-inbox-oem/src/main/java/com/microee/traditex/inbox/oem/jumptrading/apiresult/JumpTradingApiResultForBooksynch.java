package com.microee.traditex.inbox.oem.jumptrading.apiresult;

import java.io.Serializable;
import java.text.DecimalFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

public class JumpTradingApiResultForBooksynch extends JumpTradingApiResultBase
        implements Serializable {

    private static final long serialVersionUID = -5823472984673324318L;

    @JsonProperty("FIXSeq")
    private Integer fixSeq;
    @JsonProperty("Symbol")
    private String symbol;
    @JsonProperty("QuoteID")
    private String quoteId;
    @JsonProperty("timestamp")
    private Double timestamp; // new DecimalFormat("#0.000").format(boosSynch.getTimestamp())
    @JsonProperty("clientTS")
    private String clientTs;
    @JsonProperty("seq")
    private Long seq;

    public Integer getFixSeq() {
        return fixSeq;
    }

    public void setFixSeq(Integer fixSeq) {
        this.fixSeq = fixSeq;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(String quoteId) {
        this.quoteId = quoteId;
    }

    public Long getTimestamp() {
        // 科学计数
        return Long.parseLong(new DecimalFormat("#0.000").format(this.timestamp).replace(".", ""));
    }

    public void setTimestamp(Double timestamp) {
        this.timestamp = timestamp;
    }

    public String getClientTs() {
        return clientTs;
    }

    public void setClientTs(String clientTs) {
        this.clientTs = clientTs;
    }

    public Long getSeq() {
        return seq;
    }

    public void setSeq(Long seq) {
        this.seq = seq;
    }

}
