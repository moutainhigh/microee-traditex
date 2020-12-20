package com.microee.traditex.inbox.oem.cumberland.wsmessage;

import java.io.Serializable;
import java.util.UUID;
import com.microee.plugin.http.assets.HttpAssets;
import com.microee.traditex.inbox.oem.cumberland.entity.CBExternalIdentity;
import com.microee.traditex.inbox.oem.cumberland.entity.CBResponseId;

public class QuoteCloseRequestMessage implements Serializable {

    private static final long serialVersionUID = 8316073849981574168L;
    private static final String MESSAGE_TYPE = "STREAMING_QUOTE_CLOSE_REQUEST";

    private String messageType;
    private CBResponseId quoteResponseId;
    private String counterpartyRequestId;
    private CBExternalIdentity externalIdentity;
    
    public QuoteCloseRequestMessage() {
        
    }
    
    public QuoteCloseRequestMessage(String counterpartyId, String userId, CBResponseId quoteResponseId) {
        this.messageType = MESSAGE_TYPE;
        this.counterpartyRequestId = UUID.randomUUID().toString();
        this.externalIdentity = new CBExternalIdentity(counterpartyId, userId);
        this.quoteResponseId = quoteResponseId;
    }
    
    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
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
    
    public String json() {
        return HttpAssets.toJsonString(this);
    }

    public CBResponseId getQuoteResponseId() {
        return quoteResponseId;
    }

    public void setQuoteResponseId(CBResponseId quoteResponseId) {
        this.quoteResponseId = quoteResponseId;
    }
    
}
