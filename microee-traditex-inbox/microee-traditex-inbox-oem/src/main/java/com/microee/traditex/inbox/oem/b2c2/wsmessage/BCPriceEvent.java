package com.microee.traditex.inbox.oem.b2c2.wsmessage;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import com.microee.traditex.inbox.oem.b2c2.entity.Trade;

public class BCPriceEvent extends BCEventBase implements Serializable {

    private static final long serialVersionUID = 1988549627665048923L;

    private String instrument;
    private HashMap<String, List<Trade>> levels;
    private String timestamp;
    
    public BCPriceEvent() {
        
    }

    public String getInstrument() {
        return instrument;
    }

    public void setInstrument(String instrument) {
        this.instrument = instrument;
    }

    public HashMap<String, List<Trade>> getLevels() {
        return levels;
    }

    public void setLevels(HashMap<String, List<Trade>> levels) {
        this.levels = levels;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    
}
