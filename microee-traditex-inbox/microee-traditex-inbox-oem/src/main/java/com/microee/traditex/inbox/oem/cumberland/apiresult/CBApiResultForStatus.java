package com.microee.traditex.inbox.oem.cumberland.apiresult;

import java.io.Serializable;
import java.util.Map;

public class CBApiResultForStatus extends CBApiResultBase implements Serializable {

    private static final long serialVersionUID = -767032321947465505L;

    private String systemStatus;
    private Map<String, String> requestTypes;

    public String getSystemStatus() {
        return systemStatus;
    }

    public void setSystemStatus(String systemStatus) {
        this.systemStatus = systemStatus;
    }

    public Map<String, String> getRequestTypes() {
        return requestTypes;
    }

    public void setRequestTypes(Map<String, String> requestTypes) {
        this.requestTypes = requestTypes;
    }

}
