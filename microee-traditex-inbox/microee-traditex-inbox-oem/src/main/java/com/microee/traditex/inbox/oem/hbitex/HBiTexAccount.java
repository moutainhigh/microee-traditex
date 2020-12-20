package com.microee.traditex.inbox.oem.hbitex;

import java.io.Serializable;

public class HBiTexAccount implements Serializable {

    private static final long serialVersionUID = -5634093396186479530L;
    
    private String uid;
    private String accountSpotId;
    private String accesskey;
    private String secreckey;
    private String exchangeCode;
    
    public HBiTexAccount () {
        
    }

    public HBiTexAccount(String uid, String accountSpotId, String accesskey, String secreckey, String exchangeCode) {
        this.uid = uid;
        this.accountSpotId = accountSpotId;
        this.accesskey = accesskey;
        this.secreckey = secreckey;
        this.exchangeCode = exchangeCode;
    }
    
    public HBiTexAccount(String accountSpotId, String accesskey, String secreckey, String exchangeCode) {
        this.accountSpotId = accountSpotId;
        this.accesskey = accesskey;
        this.secreckey = secreckey;
        this.exchangeCode = exchangeCode;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAccountSpotId() {
        return accountSpotId;
    }

    public void setAccountSpotId(String accountSpotId) {
        this.accountSpotId = accountSpotId;
    }

    public String getAccesskey() {
        return accesskey;
    }

    public void setAccesskey(String accesskey) {
        this.accesskey = accesskey;
    }

    public String getSecreckey() {
        return secreckey;
    }

    public void setSecreckey(String secreckey) {
        this.secreckey = secreckey;
    }

    public String getExchangeCode() {
        return exchangeCode;
    }

    public void setExchangeCode(String exchangeCode) {
        this.exchangeCode = exchangeCode;
    }

}
