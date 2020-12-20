package com.microee.traditex.inbox.oem.jumptrading.apiresult;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.microee.traditex.inbox.oem.jumptrading.wsmessage.inter.JTOrderStatusDetail;

public class JumpTradingApiResultForOrderStatus extends JumpTradingApiResultBase implements Serializable {

    private static final long serialVersionUID = -6856659703839959349L;
    @JsonProperty("execs")
    private LinkedHashMap<String, List<JTOrderStatusDetail>> execs;

    public LinkedHashMap<String, List<JTOrderStatusDetail>> getExecs() {
        return execs;
    }

    public void setExecs(LinkedHashMap<String, List<JTOrderStatusDetail>> execs) {
        this.execs = execs;
    }
}
