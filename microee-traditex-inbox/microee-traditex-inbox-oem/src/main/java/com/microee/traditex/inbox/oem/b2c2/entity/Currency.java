package com.microee.traditex.inbox.oem.b2c2.entity;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Currency implements Serializable {

    private static final long serialVersionUID = -5704796785967496142L;

    @JsonProperty("stable_coin")
    private Boolean stableCoin;
    @JsonProperty("is_crypto")
    private Boolean isCrypto;
    @JsonProperty("readable_name")
    private String readableName;
    @JsonProperty("long_only")
    private Boolean longOnly;
    @JsonProperty("minimum_trade_size")
    private Double minimumTradeSize;

    public Boolean getStableCoin() {
        return stableCoin;
    }

    public void setStableCoin(Boolean stableCoin) {
        this.stableCoin = stableCoin;
    }

    public Boolean getIsCrypto() {
        return isCrypto;
    }

    public void setIsCrypto(Boolean isCrypto) {
        this.isCrypto = isCrypto;
    }

    public String getReadableName() {
        return readableName;
    }

    public void setReadableName(String readableName) {
        this.readableName = readableName;
    }

    public Boolean getLongOnly() {
        return longOnly;
    }

    public void setLongOnly(Boolean longOnly) {
        this.longOnly = longOnly;
    }

    public Double getMinimumTradeSize() {
        return minimumTradeSize;
    }

    public void setMinimumTradeSize(Double minimumTradeSize) {
        this.minimumTradeSize = minimumTradeSize;
    }

}
