package com.microee.traditex.inbox.oem.cumberland.inter;

import java.io.Serializable;
import com.microee.traditex.inbox.oem.cumberland.entity.CBExternalIdentity;
import com.microee.traditex.inbox.oem.cumberland.entity.CBResponseId;

public class TradeRequest implements Serializable {

    private static final long serialVersionUID = -5647764232233346211L;

    private String messageType = "TRADE_REQUEST";
    private CBResponseId quoteResponseId;
    private String counterpartyAction;
    private String counterpartyRequestId;
    private CBExternalIdentity externalIdentity;
    
    public TradeRequest() {
        
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public CBResponseId getQuoteResponseId() {
        return quoteResponseId;
    }

    public void setQuoteResponseId(CBResponseId quoteResponseId) {
        this.quoteResponseId = quoteResponseId;
    }

    public String getCounterpartyAction() {
        return counterpartyAction;
    }

    public void setCounterpartyAction(String counterpartyAction) {
        this.counterpartyAction = counterpartyAction;
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
