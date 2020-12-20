package com.microee.traditex.inbox.oem.cumberland.wsmessage;

import java.io.Serializable;

public class StreamEventBase implements Serializable {

    private static final long serialVersionUID = 7814549806504739942L;

    private String messageType;
    private String status;

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
