package com.microee.traditex.inbox.oem.b2c2.apiresult;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.microee.plugin.http.assets.HttpAssets;

public class B2C2ApiResultForAccountInfo implements Serializable {

    private static final long serialVersionUID = 5542412299559263082L;

    @JsonProperty("risk_exposure")
    private Double riskExposure;
    @JsonProperty("max_risk_exposure")
    private Double maxRiskExposure;
    @JsonProperty("btc_max_qty_per_trade")
    private Double btcMaxQtyPerTrade;
    @JsonProperty("ust_max_qty_per_trade")
    private Double ustMaxQtyPerTrade;
    
    public B2C2ApiResultForAccountInfo () {
        
    }

    public Double getRiskExposure() {
        return riskExposure;
    }

    public void setRiskExposure(Double riskExposure) {
        this.riskExposure = riskExposure;
    }

    public Double getMaxRiskExposure() {
        return maxRiskExposure;
    }

    public void setMaxRiskExposure(Double maxRiskExposure) {
        this.maxRiskExposure = maxRiskExposure;
    }

    public Double getBtcMaxQtyPerTrade() {
        return btcMaxQtyPerTrade;
    }

    public void setBtcMaxQtyPerTrade(Double btcMaxQtyPerTrade) {
        this.btcMaxQtyPerTrade = btcMaxQtyPerTrade;
    }

    public Double getUstMaxQtyPerTrade() {
        return ustMaxQtyPerTrade;
    }

    public void setUstMaxQtyPerTrade(Double ustMaxQtyPerTrade) {
        this.ustMaxQtyPerTrade = ustMaxQtyPerTrade;
    }
    
    @Override
    public String toString() {
        return HttpAssets.toJsonString(this);
    }

}
