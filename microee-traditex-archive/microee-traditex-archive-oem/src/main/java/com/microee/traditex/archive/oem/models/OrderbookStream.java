package com.microee.traditex.archive.oem.models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class OrderbookStream implements Serializable {
    private String orderBookId;

    private String bookId;

    private String symbol;

    private String side;

    private BigDecimal amount;

    private BigDecimal price;

    private String vender;

    private Integer calType;

    private Integer pick;

    private Integer skipCode;

    private Date emitterTime;

    private Date reveiveTime;

    private Date createdAt;

    private static final long serialVersionUID = 1L;

    public String getOrderBookId() {
        return orderBookId;
    }

    public void setOrderBookId(String orderBookId) {
        this.orderBookId = orderBookId == null ? null : orderBookId.trim();
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId == null ? null : bookId.trim();
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol == null ? null : symbol.trim();
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side == null ? null : side.trim();
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getVender() {
        return vender;
    }

    public void setVender(String vender) {
        this.vender = vender == null ? null : vender.trim();
    }

    public Integer getCalType() {
        return calType;
    }

    public void setCalType(Integer calType) {
        this.calType = calType;
    }

    public Integer getPick() {
        return pick;
    }

    public void setPick(Integer pick) {
        this.pick = pick;
    }

    public Integer getSkipCode() {
        return skipCode;
    }

    public void setSkipCode(Integer skipCode) {
        this.skipCode = skipCode;
    }

    public Date getEmitterTime() {
        return emitterTime;
    }

    public void setEmitterTime(Date emitterTime) {
        this.emitterTime = emitterTime;
    }

    public Date getReveiveTime() {
        return reveiveTime;
    }

    public void setReveiveTime(Date reveiveTime) {
        this.reveiveTime = reveiveTime;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}