package com.microee.traditex.inbox.oem.b2c2.entity;

import java.io.Serializable;
import java.math.BigDecimal;

public class Trade implements Serializable {

    private static final long serialVersionUID = -5019612932749998010L;

    private BigDecimal price;
    private BigDecimal quantity;
    
    public Trade() {
        
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

}