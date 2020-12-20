package com.microee.traditex.inbox.oem.jumptrading.apiparam;

import java.io.Serializable;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class JTOrderStatusParam implements Serializable {
    
    private static final long serialVersionUID = -4645968246951962662L;

    @JsonProperty("lstOrderIds")
    private List<String> lstOrderIds;

    public List<String> getLstOrderIds() {
        return lstOrderIds;
    }

    public void setLstOrderIds(List<String> lstOrderIds) {
        this.lstOrderIds = lstOrderIds;
    }
}
