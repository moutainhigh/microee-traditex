package com.microee.traditex.inbox.oem.hbitex.po;

import java.math.BigDecimal;

public class BigDecimalValue {

    public BigDecimal value;
    
    public BigDecimalValue(String val) {
        try {
            this.value = new BigDecimal(val);
        } catch (Exception e) {
            
        }
    }
    
}
