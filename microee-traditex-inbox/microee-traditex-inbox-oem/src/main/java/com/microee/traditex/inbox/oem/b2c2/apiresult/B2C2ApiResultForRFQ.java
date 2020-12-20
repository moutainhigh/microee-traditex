package com.microee.traditex.inbox.oem.b2c2.apiresult;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonProperty;

public class B2C2ApiResultForRFQ implements Serializable {

    private static final long serialVersionUID = -8622731924861110317L;

    @JsonProperty("valid_until")
    private String validUntil;
    @JsonProperty("rfq_id")
    private String rfqId;
    @JsonProperty("client_rfq_id")
    private String clientRfqId;
    @JsonProperty("quantity")
    private String quantity;
    @JsonProperty("side")
    private String side;
    @JsonProperty("instrument")
    private String instrument;
    @JsonProperty("price")
    private String price;
    @JsonProperty("created")
    private String created;

    public String getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(String validUntil) {
        this.validUntil = validUntil;
    }

    public String getRfqId() {
        return rfqId;
    }

    public void setRfqId(String rfqId) {
        this.rfqId = rfqId;
    }

    public String getClientRfqId() {
        return clientRfqId;
    }

    public void setClientRfqId(String clientRfqId) {
        this.clientRfqId = clientRfqId;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public String getInstrument() {
        return instrument;
    }

    public void setInstrument(String instrument) {
        this.instrument = instrument;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

}
