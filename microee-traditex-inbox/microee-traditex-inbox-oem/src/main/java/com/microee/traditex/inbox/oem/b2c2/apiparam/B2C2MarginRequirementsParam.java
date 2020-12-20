package com.microee.traditex.inbox.oem.b2c2.apiparam;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class B2C2MarginRequirementsParam implements Serializable {

    private static final long serialVersionUID = 8176948380670257190L;
    
    private String currency;
    
    public B2C2MarginRequirementsParam() {
        
    }
    
    public B2C2MarginRequirementsParam(String currency) {
        this.currency = currency;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    public Map<String, Object> param() {
        Map<String, Object> map = new HashMap<>();
        map.put("currency", this.currency);
        return map;
    }
    
    
}
