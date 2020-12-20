package com.microee.traditex.inbox.oem.cumberland.entity;

import java.io.Serializable;

public class CBTotalAmount implements Serializable {

    private static final long serialVersionUID = -2114304083582166996L;
    
    private Double amount;
    private String currency;
    
    public CBTotalAmount() {
        
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    public String string() {
        return "Total" + "=" + this.currency + "-" +  this.amount;
    }

}
