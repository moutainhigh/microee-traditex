package com.microee.traditex.inbox.oem.b2c2.apiparam;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class B2C2RequestForQuoteParam implements Serializable {

    private static final long serialVersionUID = 4438566572034529139L;
    
    private String instrument;
    private String side;
    private Integer quantity;
    private String clientRfqId;
    
    public B2C2RequestForQuoteParam() {
        
    }
    
    public B2C2RequestForQuoteParam(String instrument, Integer quantity) {
        this.instrument = instrument;
        this.side = "buy";
        this.quantity = quantity;
        this.clientRfqId = UUID.randomUUID().toString();
    }
    
    public Map<String, Object> param() {
        Map<String, Object> map = new HashMap<>();
        map.put("instrument", this.instrument);
        map.put("side", this.side);
        map.put("quantity", this.quantity);
        map.put("client_rfq_id", this.clientRfqId);
        return map;
    }
    
}
