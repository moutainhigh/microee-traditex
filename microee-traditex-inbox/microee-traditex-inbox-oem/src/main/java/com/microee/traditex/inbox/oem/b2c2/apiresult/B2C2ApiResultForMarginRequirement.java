package com.microee.traditex.inbox.oem.b2c2.apiresult;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonProperty;

public class B2C2ApiResultForMarginRequirement implements Serializable {

    private static final long serialVersionUID = -4811939034171310879L;

    @JsonProperty("margin_requirement")
    private Double marginRequirement;
    private String currency;
    @JsonProperty("margin_usage")
    private Double marginUsage;
    private Double equity;

    public Double getMarginRequirement() {
        return marginRequirement;
    }

    public void setMarginRequirement(Double marginRequirement) {
        this.marginRequirement = marginRequirement;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Double getMarginUsage() {
        return marginUsage;
    }

    public void setMarginUsage(Double marginUsage) {
        this.marginUsage = marginUsage;
    }

    public Double getEquity() {
        return equity;
    }

    public void setEquity(Double equity) {
        this.equity = equity;
    }

}
