package com.microee.traditex.inbox.oem.cumberland.wsmessage;

import java.io.Serializable;
import java.util.UUID;
import com.microee.traditex.inbox.oem.cumberland.entity.CBExternalIdentity;

public class CBTradeHistoryRequestMessage implements Serializable {

    private static final long serialVersionUID = -8195828214524449702L;
    public static final String MESSAGE_TYPE = "TRADE_HISTORY_REQUEST";

    private String messageType;
    private String counterpartyRequestId;
    private CBExternalIdentity externalIdentity;
    private int numberOfRecords;

    public CBTradeHistoryRequestMessage() {

    }

    public CBTradeHistoryRequestMessage(int numberOfRecords, String counterpartyId, String userId) {
        this.messageType = MESSAGE_TYPE;
        this.counterpartyRequestId = UUID.randomUUID().toString();
        this.numberOfRecords = numberOfRecords;
        this.externalIdentity = new CBExternalIdentity(counterpartyId, userId);
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

    public int getNumberOfRecords() {
        return numberOfRecords;
    }

    public void setNumberOfRecords(int numberOfRecords) {
        this.numberOfRecords = numberOfRecords;
    }

}
