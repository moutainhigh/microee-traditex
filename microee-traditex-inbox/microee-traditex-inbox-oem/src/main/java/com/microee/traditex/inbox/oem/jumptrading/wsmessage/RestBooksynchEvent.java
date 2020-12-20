package com.microee.traditex.inbox.oem.jumptrading.wsmessage;

import java.io.Serializable;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.microee.traditex.inbox.oem.jumptrading.apiresult.JumpTradingApiResultForBooksynch;
import com.microee.traditex.inbox.oem.jumptrading.wsmessage.inter.BooksynchInterSize;

public class RestBooksynchEvent extends JumpTradingApiResultForBooksynch implements Serializable {

    private static final long serialVersionUID = -5325044481425918312L;
    /**
     * 卖
     */
    @JsonProperty("1")
    private Map<String, BooksynchInterSize> asks; // 卖
    /**
     * 买
     */
    @JsonProperty("0")
    private Map<String, BooksynchInterSize> bids; // 买

    public Map<String, BooksynchInterSize> getAsks() {
        return asks;
    }

    public void setAsks(Map<String, BooksynchInterSize> asks) {
        this.asks = asks;
    }

    public Map<String, BooksynchInterSize> getBids() {
        return bids;
    }

    public void setBids(Map<String, BooksynchInterSize> bids) {
        this.bids = bids;
    }

}
