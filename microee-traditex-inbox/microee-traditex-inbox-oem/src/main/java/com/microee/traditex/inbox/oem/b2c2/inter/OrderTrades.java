package com.microee.traditex.inbox.oem.b2c2.inter;

import java.io.Serializable;
import java.math.BigDecimal;
import org.joda.time.format.ISODateTimeFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

public class OrderTrades implements Serializable {

    private static final long serialVersionUID = -2631127412108753207L;

    @JsonProperty("trade_id")
    private String tradeId;
    @JsonProperty("rfq_id")
    private String rfqId;
    @JsonProperty("cfd_contract")
    private String cfdContract;
    @JsonProperty("order")
    private String order;
    @JsonProperty("quantity")
    private BigDecimal quantity;
    @JsonProperty("side")
    private String side;
    @JsonProperty("instrument")
    private String instrument;
    @JsonProperty("price")
    private BigDecimal price;
    @JsonProperty("created")
    private String created; // 2020-07-06T12:43:17.400259Z
    @JsonProperty("origin")
    private String origin;
    @JsonProperty("executing_unit")
    private String executingUnit;

    public OrderTrades() {

    }

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public String getRfqId() {
        return rfqId;
    }

    public void setRfqId(String rfqId) {
        this.rfqId = rfqId;
    }

    public String getCfdContract() {
        return cfdContract;
    }

    public void setCfdContract(String cfdContract) {
        this.cfdContract = cfdContract;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Long getCreated() {
        return ISODateTimeFormat.dateTimeParser().parseDateTime(this.created).getMillis();
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getExecutingUnit() {
        return executingUnit;
    }

    public void setExecutingUnit(String executingUnit) {
        this.executingUnit = executingUnit;
    }

}
