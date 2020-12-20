package com.microee.traditex.inbox.oem.cumberland.wsmessage;

import java.io.Serializable;
import java.math.BigDecimal;

public class CBTradeRequestParam implements Serializable {

    private static final long serialVersionUID = -3887108754771529997L;

    /**
     * 交易对
     */
    private String symbol;

    /**
     * 外部订单号唯一（查询历史会反馈）
     */
    private String clOrdId;
    /**
     * 目标币数量
     */
    private BigDecimal quantity;
    /**
     * 目标币
     */
    private String currency;

    /**
     * 目前支持2(市价) 1限价暂时不考虑
     */
    private String ordType;

    /**
     * 可选 BUY 或 SELL
     */
    private String side;
    /**
     * 预留用于限价使用
     */
    private BigDecimal price;

    public CBTradeRequestParam() {

    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getClOrdId() {
        return clOrdId;
    }

    public void setClOrdId(String clOrdId) {
        this.clOrdId = clOrdId;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getOrdType() {
        return ordType;
    }

    public void setOrdType(String ordType) {
        this.ordType = ordType;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public QuoteRequestMessage getQuoteParam(String baseCurrency, String quoteCurrency, String requestId, String counterPartyId,
                                             String userId, BigDecimal quantity) {
        String symbol = String.format("%s_%s", baseCurrency, quoteCurrency); // ETH_JPY
        return new QuoteRequestMessage(requestId, symbol, baseCurrency, quantity, counterPartyId, userId);
    }

}
