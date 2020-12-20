package com.microee.traditex.inbox.oem.cumberland.inter;


import java.io.Serializable;
import com.microee.traditex.inbox.oem.cumberland.entity.CBExternalIdentity;
import com.microee.traditex.inbox.oem.cumberland.entity.CBQuantity;
import com.microee.traditex.inbox.oem.cumberland.entity.CBResponseId;
import com.microee.traditex.inbox.oem.cumberland.entity.CBTotalAmount;
import com.microee.traditex.inbox.oem.cumberland.entity.CBUnitPrice;
import com.microee.traditex.inbox.oem.cumberland.wsmessage.StreamQuoteEvent;

/**
 * 交易返回
 */
public class TradeResponse implements Serializable {

    private static final long serialVersionUID = 5004726284985117698L;

    private String messageType = "TRADE_RESPONSE";
    private CBResponseId tradeResponseId;
    private TradeRequest tradeRequest;
    private StreamQuoteEvent quoteResponse;
    private CBExternalIdentity externalIdentity;
    private String status;
    private String tradeId;
    private long tradeTime;
    private String counterpartyAction;
    private CBQuantity quantity;
    private CBUnitPrice unitPrice;
    private CBTotalAmount totalAmount;
    
    public TradeResponse() {
        
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public CBResponseId getTradeResponseId() {
        return tradeResponseId;
    }

    public void setTradeResponseId(CBResponseId tradeResponseId) {
        this.tradeResponseId = tradeResponseId;
    }

    public TradeRequest getTradeRequest() {
        return tradeRequest;
    }

    public void setTradeRequest(TradeRequest tradeRequest) {
        this.tradeRequest = tradeRequest;
    }

    public StreamQuoteEvent getQuoteResponse() {
        return quoteResponse;
    }

    public void setQuoteResponse(StreamQuoteEvent quoteResponse) {
        this.quoteResponse = quoteResponse;
    }

    public CBExternalIdentity getExternalIdentity() {
        return externalIdentity;
    }

    public void setExternalIdentity(CBExternalIdentity externalIdentity) {
        this.externalIdentity = externalIdentity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public Long getTradeTime() {
        return tradeTime;
    }

    public void setTradeTime(Long tradeTime) {
        this.tradeTime = tradeTime;
    }

    public String getCounterpartyAction() {
        return counterpartyAction;
    }

    public void setCounterpartyAction(String counterpartyAction) {
        this.counterpartyAction = counterpartyAction;
    }

    public CBQuantity getQuantity() {
        return quantity;
    }

    public void setQuantity(CBQuantity quantity) {
        this.quantity = quantity;
    }

    public CBUnitPrice getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(CBUnitPrice unitPrice) {
        this.unitPrice = unitPrice;
    }

    public CBTotalAmount getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(CBTotalAmount totalAmount) {
        this.totalAmount = totalAmount;
    }

}
