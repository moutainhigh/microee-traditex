package com.microee.traditex.inbox.oem.jumptrading.wsmessage.inter;

import java.io.Serializable;
import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.microee.traditex.inbox.oem.jumptrading.apiresult.JumpTradingApiResultBase;

public class JTOrderStatusDetail extends JumpTradingApiResultBase implements Serializable {

    private static final long serialVersionUID = -300959771307015117L;

    @JsonProperty("timestamp")
    private Long timestamp;

    @JsonProperty("ClOrdID")
    private String clOrdID;

    @JsonProperty("OrderID")
    private String orderID;

    @JsonProperty("ExecID")
    private String execID;

    /**
     * Status include: '0' (New), '1' (PartialFill'), '2' (Filled), '3' (DoneForDay), '4'
     * (Cancelled) or '8' (Rejected)
     */
    @JsonProperty("OrdStatus")
    private String ordStatus;

    @JsonProperty("ExecType")
    private String execType;

    @JsonProperty("ExecTransType")
    private String execTransType;

    @JsonProperty("Symbol")
    private String symbol;

    @JsonProperty("Side")
    private String side;

    @JsonProperty("OrdType")
    private String ordType;

    @JsonProperty("TransactTime")
    private String transactTime;

    @JsonProperty("ClientID")
    private String clientID;

    @JsonProperty("OrderQty")
    private BigDecimal orderQty;

    @JsonProperty("Price")
    private BigDecimal price;

    @JsonProperty("LeavesQty")
    private BigDecimal leavesQty;

    @JsonProperty("CumQty")
    private BigDecimal cumQty;

    @JsonProperty("avgPx")
    private BigDecimal AvgPx;

    @JsonProperty("BookType")
    private String bookType;

    @JsonProperty("seq")
    private Long seq;

    @JsonProperty("clientTS")
    private String clientTS;

    @JsonProperty("Account")
    private String account;

    @JsonProperty("Text")
    private String text;

    /**
     * 成交数量
     */
    @JsonProperty("LastQty")
    private BigDecimal lastQty;

    /**
     * 成交价格
     */
    @JsonProperty("LastPx")
    private BigDecimal lastPx;

    @JsonProperty("SettlDate")
    private String settlDate;

    @JsonProperty("SettlCurrAmt")
    private String settlCurrAmt;

    @JsonProperty("SettlCurrency")
    private String settlCurrency;

    public Long getTimestamp() {
        // if (timestamp == null) {
        // return null;
        // }
        // // 禁用科学计数
        // return Long.parseLong(new DecimalFormat("#0.000").format(timestamp).replace(".", ""));
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getClOrdID() {
        return clOrdID;
    }

    public void setClOrdID(String clOrdID) {
        this.clOrdID = clOrdID;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getExecID() {
        return execID;
    }

    public void setExecID(String execID) {
        this.execID = execID;
    }

    public String getOrdStatus() {
        return ordStatus;
    }

    public void setOrdStatus(String ordStatus) {
        this.ordStatus = ordStatus;
    }

    public String getExecType() {
        return execType;
    }

    public void setExecType(String execType) {
        this.execType = execType;
    }

    public String getExecTransType() {
        return execTransType;
    }

    public void setExecTransType(String execTransType) {
        this.execTransType = execTransType;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public String getOrdType() {
        return ordType;
    }

    public void setOrdType(String ordType) {
        this.ordType = ordType;
    }

    public String getTransactTime() {
        return transactTime;
    }

    public void setTransactTime(String transactTime) {
        this.transactTime = transactTime;
    }

    public String getClientID() {
        return clientID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    public BigDecimal getOrderQty() {
        return orderQty;
    }

    public void setOrderQty(BigDecimal orderQty) {
        this.orderQty = orderQty;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getLeavesQty() {
        return leavesQty;
    }

    public void setLeavesQty(BigDecimal leavesQty) {
        this.leavesQty = leavesQty;
    }

    public BigDecimal getCumQty() {
        return cumQty;
    }

    public void setCumQty(BigDecimal cumQty) {
        this.cumQty = cumQty;
    }

    public BigDecimal getAvgPx() {
        return AvgPx;
    }

    public void setAvgPx(BigDecimal avgPx) {
        AvgPx = avgPx;
    }

    public String getBookType() {
        return bookType;
    }

    public void setBookType(String bookType) {
        this.bookType = bookType;
    }

    public Long getSeq() {
        return seq;
    }

    public void setSeq(Long seq) {
        this.seq = seq;
    }

    public String getClientTS() {
        return clientTS;
    }

    public void setClientTS(String clientTS) {
        this.clientTS = clientTS;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public BigDecimal getLastQty() {
        return lastQty;
    }

    public void setLastQty(BigDecimal lastQty) {
        this.lastQty = lastQty;
    }

    public BigDecimal getLastPx() {
        return lastPx;
    }

    public void setLastPx(BigDecimal lastPx) {
        this.lastPx = lastPx;
    }

    public String getSettlDate() {
        return settlDate;
    }

    public void setSettlDate(String settlDate) {
        this.settlDate = settlDate;
    }

    public String getSettlCurrAmt() {
        return settlCurrAmt;
    }

    public void setSettlCurrAmt(String settlCurrAmt) {
        this.settlCurrAmt = settlCurrAmt;
    }

    public String getSettlCurrency() {
        return settlCurrency;
    }

    public void setSettlCurrency(String settlCurrency) {
        this.settlCurrency = settlCurrency;
    }
    
}
