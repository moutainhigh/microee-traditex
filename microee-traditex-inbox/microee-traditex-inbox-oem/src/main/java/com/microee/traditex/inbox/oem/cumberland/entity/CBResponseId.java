package com.microee.traditex.inbox.oem.cumberland.entity;

import java.io.Serializable;

/**
 * 报价返回id
 */
public class CBResponseId implements Serializable {
    
    private static final long serialVersionUID = -9064418436617800781L;

    /**
     * version : 1
     * id : 36e60dd7-8f75-4d8d-9f45-a9ba54489b2c
     */
    
    public CBResponseId() {
        
    }
    
    public CBResponseId(int version, String id) {
        this.version = version;
        this.id = id;
    }

    public CBResponseId(String id) {
        this.id = id;
    }
    
    private int version;
    private String id;

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
