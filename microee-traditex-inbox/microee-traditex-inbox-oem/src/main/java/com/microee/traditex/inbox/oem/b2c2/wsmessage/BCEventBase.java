package com.microee.traditex.inbox.oem.b2c2.wsmessage;

import java.io.Serializable;

public class BCEventBase implements Serializable {

    private static final long serialVersionUID = 1892593366501109460L;

    private String event;
    private Boolean success;
    
    public BCEventBase() {
        
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

}
