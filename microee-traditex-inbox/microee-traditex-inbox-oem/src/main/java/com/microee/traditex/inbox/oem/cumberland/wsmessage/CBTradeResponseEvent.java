package com.microee.traditex.inbox.oem.cumberland.wsmessage;

import java.io.Serializable;
import com.microee.traditex.inbox.oem.cumberland.entity.CBExternalIdentity;
import com.microee.traditex.inbox.oem.cumberland.entity.CBQuantity;
import com.microee.traditex.inbox.oem.cumberland.entity.CBResponseId;
import com.microee.traditex.inbox.oem.cumberland.entity.CBTotalAmount;
import com.microee.traditex.inbox.oem.cumberland.entity.CBUnitPrice;

public class CBTradeResponseEvent implements Serializable {

    private static final long serialVersionUID = -7206252664842485255L;

    private String messageType = "TRADE_RESPONSE";
    private CBResponseId tradeResponseId;
    private CBTradeRequestMessage tradeRequest;
    private CBResponseId quoteResponse;
    private CBExternalIdentity externalIdentity;
    private String status;
    private String tradeId;
    private long tradeTime;
    private String counterpartyAction;
    private CBQuantity quantity;
    private CBUnitPrice unitPrice;
    private CBTotalAmount totalAmount;

    public CBTradeResponseEvent() {

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

    public CBTradeRequestMessage getTradeRequest() {
        return tradeRequest;
    }

    public void setTradeRequest(CBTradeRequestMessage tradeRequest) {
        this.tradeRequest = tradeRequest;
    }

    public CBResponseId getQuoteResponse() {
        return quoteResponse;
    }

    public void setQuoteResponse(CBResponseId quoteResponse) {
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

    public long getTradeTime() {
        return tradeTime;
    }

    public void setTradeTime(long tradeTime) {
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
