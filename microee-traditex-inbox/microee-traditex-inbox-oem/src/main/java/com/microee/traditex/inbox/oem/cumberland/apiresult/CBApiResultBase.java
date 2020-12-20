package com.microee.traditex.inbox.oem.cumberland.apiresult;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class CBApiResultBase implements Serializable {

    private static final long serialVersionUID = -2495366122910418228L;

    private String messageType;
    private String counterpartyRequestId;

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

    @JsonIgnore
    public boolean success() {
        return true;
    }

}
