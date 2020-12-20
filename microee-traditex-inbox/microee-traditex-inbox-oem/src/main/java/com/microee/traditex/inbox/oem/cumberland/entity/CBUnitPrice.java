package com.microee.traditex.inbox.oem.cumberland.entity;

import java.io.Serializable;
import java.math.BigDecimal;

public class CBUnitPrice implements Serializable {

    private static final long serialVersionUID = 5708675663905818475L;

    private BigDecimal price;
    private String ticker;
    private String currency;
    
    public CBUnitPrice() {
        
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String string() {
        return "Price" + "=" + this.currency + "-" + this.ticker + "-" + this.price;
    }


}
