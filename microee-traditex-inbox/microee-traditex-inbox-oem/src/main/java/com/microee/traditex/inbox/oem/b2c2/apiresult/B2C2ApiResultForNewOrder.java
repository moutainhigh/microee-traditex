package com.microee.traditex.inbox.oem.b2c2.apiresult;

import java.io.Serializable;
import java.util.List;
import org.joda.time.format.ISODateTimeFormat; 
import com.fasterxml.jackson.annotation.JsonProperty;
import com.microee.traditex.inbox.oem.b2c2.inter.OrderTrades;

//@formatter:off
//{
//    "order_id": "01bc43e7-f5b6-4ff5-8404-51c1f56c3c30",
//    "client_order_id": "12b73b5a-cb70-47d5-92bc-0c764557f4e0",
//    "quantity": "1.0000000000",
//    "side": "buy",
//    "instrument": "BTCUST.SPOT",
//    "price": "1000.00000000",
//    "executed_price": null,
//    "trades": [],
//    "created": "2020-07-05T09:05:14.452769Z"
//}
//@formatter:on
public class B2C2ApiResultForNewOrder implements Serializable {

    private static final long serialVersionUID = -8251192989337419829L;

    @JsonProperty("order_id")
    private String orderId;
    @JsonProperty("client_order_id")
    private String clientOrderId;
    @JsonProperty("quantity")
    private String quantity;
    @JsonProperty("side")
    private String side;
    @JsonProperty("instrument")
    private String instrument;
    @JsonProperty("price")
    private String price;
    @JsonProperty("executed_price")
    private String executedPrice;
    @JsonProperty("trades")
    private List<OrderTrades> trades;
    @JsonProperty("created")
    private String created; // 2020-07-06T12:43:17.400259Z

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getClientOrderId() {
        return clientOrderId;
    }

    public void setClientOrderId(String clientOrderId) {
        this.clientOrderId = clientOrderId;
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

    public String getExecutedPrice() {
        return executedPrice;
    }

    public void setExecutedPrice(String executedPrice) {
        this.executedPrice = executedPrice;
    }

    public List<OrderTrades> getTrades() {
        return trades;
    }

    public void setTrades(List<OrderTrades> trades) {
        this.trades = trades;
    }

    public Long getCreated() {
        return ISODateTimeFormat.dateTimeParser().parseDateTime(this.created).getMillis();
    }

    public void setCreated(String created) {
        this.created = created;
    }

}
