package com.microee.traditex.inbox.oem.cumberland.wsmessage;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;
import com.microee.plugin.http.assets.HttpAssets;
import com.microee.traditex.inbox.oem.cumberland.entity.CBExternalIdentity;
import com.microee.traditex.inbox.oem.cumberland.entity.CBQuantity;

public class QuoteRequestMessage implements Serializable {

    private static final long serialVersionUID = 7479463813225805272L;
    private static final String MESSAGE_TYPE = "STREAMING_QUOTE_REQUEST";

    private String messageType;
    private String ticker;
    private String counterpartyRequestId;
    private CBQuantity quantity;
    private CBExternalIdentity externalIdentity;
    
    public QuoteRequestMessage() {
        
    }
    
    public QuoteRequestMessage(String ticker, String quoteCurrency, BigDecimal quantity, String counterpartyId, String userId) {
        this.messageType = MESSAGE_TYPE;
        this.ticker = ticker;
        this.counterpartyRequestId = UUID.randomUUID().toString();
        this.quantity = new CBQuantity(quoteCurrency, quantity);
        this.externalIdentity = new CBExternalIdentity(counterpartyId, userId);
    }
    
    public QuoteRequestMessage(String orderId, String ticker, String currency, BigDecimal quantity, String counterpartyId, String userId) {
        this.messageType = MESSAGE_TYPE;
        this.ticker = ticker;
        this.counterpartyRequestId = orderId;
        this.quantity = new CBQuantity(currency, quantity);
        this.externalIdentity = new CBExternalIdentity(counterpartyId, userId);
    }
    
    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public CBQuantity getQuantity() {
        return quantity;
    }

    public void setQuantity(CBQuantity quantity) {
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
    
    public String json() {
        return HttpAssets.toJsonString(this);
    }


}
