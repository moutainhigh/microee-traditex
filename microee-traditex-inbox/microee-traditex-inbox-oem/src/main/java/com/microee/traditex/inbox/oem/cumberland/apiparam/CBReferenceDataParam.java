package com.microee.traditex.inbox.oem.cumberland.apiparam;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class CBReferenceDataParam extends CBParamBase implements Serializable {

    private static final long serialVersionUID = 3265062091406365651L;

    private String counterpartyId;

    public CBReferenceDataParam() {
        super();
    }

    public CBReferenceDataParam(String counterpartyId) {
        this.counterpartyId = counterpartyId;
    }
    
    public String getCounterpartyId() {
        return counterpartyId;
    }

    public void setCounterpartyId(String counterpartyId) {
        this.counterpartyId = counterpartyId;
    }

    public Map<String, Object> queryParam() {
        Map<String, Object> map = new HashMap<>();        
        map.put("counterpartyId", this.getCounterpartyId());
        map.put("counterpartyRequestId", this.getCounterpartyRequestId());
        return map;
    }
}
