package com.microee.traditex.inbox.oem.cumberland.inter;

import java.io.Serializable;
import com.microee.traditex.inbox.oem.cumberland.entity.CBExternalIdentity;

public class QuoteRequest implements Serializable {

    private static final long serialVersionUID = 3883067139407388650L;

    private String messageType;
    private Long receivedTime;
    private String ticker;
    private Object quantity;
    private String counterpartyRequestId;
    private CBExternalIdentity externalIdentity;

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public Long getReceivedTime() {
        return receivedTime;
    }

    public void setReceivedTime(Long receivedTime) {
        this.receivedTime = receivedTime;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public Object getQuantity() {
        return quantity;
    }

    public void setQuantity(Object quantity) {
        this.quantity = quantity;
    }

    public String getCounterpartyRequestId() {
        return counterpartyRequestId;
    }

    public void setCounterpartyRequestId(String counterpartyRequestId) {
        this.counterpartyRequestId = counterpartyRequestId;
    }

    public CBExternalIdentity getExternalIdentity() {
        return externalIdentity;
    }

    public void setExternalIdentity(CBExternalIdentity externalIdentity) {
        this.externalIdentity = externalIdentity;
    }

}