package com.microee.traditex.inbox.oem.cumberland.apiparam;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CBParamBase implements Serializable {

    private static final long serialVersionUID = 3265062091406365651L;

    private String counterpartyRequestId;

    public CBParamBase() {
        this.counterpartyRequestId = UUID.randomUUID().toString();
    }

    public String getCounterpartyRequestId() {
        return counterpartyRequestId;
    }

    public void setCounterpartyRequestId(String counterpartyRequestId) {
        this.counterpartyRequestId = counterpartyRequestId;
    }
    
    public Map<String, Object> queryParam() {
        Map<String, Object> map = new HashMap<> ();
        map.put("counterpartyRequestId", this.getCounterpartyRequestId());
        return map;
    }

}
