package com.microee.traditex.inbox.oem.jumptrading.wsmessage.inter;

import java.io.Serializable;
import java.math.BigDecimal;

public class BooksynchInterSize implements Serializable {
    
    private static final long serialVersionUID = 5910968261235787690L;
    
    private BigDecimal size;
    
    public BooksynchInterSize() {
        
    }
    
    public BooksynchInterSize(BigDecimal size) {
        this.size = size;
    }

    public BigDecimal getSize() {
        return size;
    }

    public void setSize(BigDecimal size) {
        this.size = size;
    }
    
}
