package com.microee.traditex.inbox.oem.b2c2.apiparam;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class B2C2NewOrderParam implements Serializable {

    private static final long serialVersionUID = 8938408688394080448L;

    private String instrument;
    private String side;
    private BigDecimal quantity;
    private String clientOrderId;
    private BigDecimal price;
    private String orderType;

    public B2C2NewOrderParam() {

    }

    public B2C2NewOrderParam(String clientOrderId, String instrument, String side, BigDecimal quantity,
            BigDecimal price) {
        this.instrument = instrument;
        this.side = side;
        this.quantity = quantity;
        this.clientOrderId = clientOrderId;
        this.price = price;
        this.orderType = "FOK";
    }

    public String getInstrument() {
        return instrument;
    }

    public void setInstrument(String instrument) {
        this.instrument = instrument;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public String getClientOrderId() {
        return clientOrderId;
    }

    public void setClientOrderId(String clientOrderId) {
        this.clientOrderId = clientOrderId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public Map<String, Object> param() {
        Map<String, Object> map = new HashMap<>();
        map.put("instrument", this.instrument + ".SPOT");
        map.put("side", this.side);
        map.put("quantity", this.quantity.toString());
        map.put("client_order_id", this.clientOrderId);
        map.put("price", this.price.toString());
        map.put("order_type", this.orderType);
        map.put("valid_until", validUntil());
        return map;
    }
    
    private static String validUntil() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MINUTE, 20);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(calendar.getTime()).replace(" ", "T");
    }

}
