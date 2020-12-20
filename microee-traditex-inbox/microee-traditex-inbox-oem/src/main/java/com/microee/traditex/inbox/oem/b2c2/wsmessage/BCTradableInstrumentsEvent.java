package com.microee.traditex.inbox.oem.b2c2.wsmessage;

import java.io.Serializable;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BCTradableInstrumentsEvent extends BCEventBase implements Serializable {

    private static final long serialVersionUID = -2367287740100886716L;

    @JsonProperty("tradable_instruments")
    private List<String> tradableInstruments;
    
    public BCTradableInstrumentsEvent() {
        
    }

    public List<String> getTradableInstruments() {
        return tradableInstruments;
    }

    public void setTradableInstruments(List<String> tradableInstruments) {
        this.tradableInstruments = tradableInstruments;
    }

}
