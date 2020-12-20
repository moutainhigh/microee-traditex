package com.microee.traditex.inbox.oem.cumberland.entity;

import java.io.Serializable;

/**
 * 身份验证
 */
public class CBExternalIdentity implements Serializable {
    
    private static final long serialVersionUID = -8049053088670064850L;

    private String counterpartyId;
    private String userId;
    
    public CBExternalIdentity() {
        
    }

    public CBExternalIdentity(String counterpartyId, String userId) {
        this.counterpartyId = counterpartyId;
        this.userId = userId;
    }

    public String getCounterpartyId() {
        return counterpartyId;
    }

    public void setCounterpartyId(String counterpartyId) {
        this.counterpartyId = counterpartyId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
