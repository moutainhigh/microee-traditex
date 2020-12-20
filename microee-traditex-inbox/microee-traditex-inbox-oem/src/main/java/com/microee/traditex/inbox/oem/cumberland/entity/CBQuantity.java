package com.microee.traditex.inbox.oem.cumberland.entity;

import java.io.Serializable;
import java.math.BigDecimal;

public class CBQuantity implements Serializable {

    private static final long serialVersionUID = 6941944785315301143L;

    private BigDecimal quantity;
    private String currency;
    
    public CBQuantity() {
        
    }
    
    public CBQuantity(String quoteCurrency, BigDecimal quantity) {
        this.currency = quoteCurrency;
        this.quantity = quantity;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    public String string() {
        return "Quantity" + "=" + this.currency + "-" +  this.quantity;
    }

}
