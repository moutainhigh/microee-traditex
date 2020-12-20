package com.microee.traditex.inbox.oem.hbitex.vo;


import java.io.Serializable;

public class AccountBalanceList implements Serializable {

    private static final long serialVersionUID = -7585525927649290785L;

    private String currency;
    private String type;
    private String balance;
    
    public AccountBalanceList() {
        
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

}
