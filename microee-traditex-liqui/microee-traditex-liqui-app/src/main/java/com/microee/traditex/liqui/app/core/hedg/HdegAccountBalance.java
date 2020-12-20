package com.microee.traditex.liqui.app.core.hedg;

public class HdegAccountBalance {

    private String account;
    private String instrument;
    private String balance;
    
    public HdegAccountBalance(String account, String instrument, String balance) {
        this.account = account;
        this.instrument = instrument;
        this.balance = balance;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getInstrument() {
        return instrument;
    }

    public void setInstrument(String instrument) {
        this.instrument = instrument;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }
    
}
