package com.microee.traditex.inbox.oem.jumptrading.apiresult;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonProperty;

public class JumpTradingApiResultForHeartbeat extends JumpTradingApiResultBase
        implements Serializable {

    private static final long serialVersionUID = 3882904458744233610L;

    @JsonProperty("StreamTimeout")
    private Double streamTimeout;

    public Double getStreamTimeout() {
        return streamTimeout;
    }

    public void setStreamTimeout(Double streamTimeout) {
        this.streamTimeout = streamTimeout;
    }

}
