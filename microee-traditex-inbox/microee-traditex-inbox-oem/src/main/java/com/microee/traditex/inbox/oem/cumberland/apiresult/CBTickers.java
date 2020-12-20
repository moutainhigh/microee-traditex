package com.microee.traditex.inbox.oem.cumberland.apiresult;

import java.io.Serializable;
import java.math.BigDecimal;

public class CBTickers implements Serializable {

    private static final long serialVersionUID = 6821695899062634919L;

    private String id;
    private String base;
    private String counter;
    private Integer priceScale;
    private Integer quantityScale;
    private Integer amountScale;
    private Integer bigNumberStart;
    private Integer bigNumberWidth;
    private String status;
    private Boolean enabled;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getCounter() {
        return counter;
    }

    public void setCounter(String counter) {
        this.counter = counter;
    }

    public Integer getPriceScale() {
        return priceScale;
    }

    public void setPriceScale(Integer priceScale) {
        this.priceScale = priceScale;
    }

    public Integer getQuantityScale() {
        return quantityScale;
    }

    public void setQuantityScale(Integer quantityScale) {
        this.quantityScale = quantityScale;
    }

    public Integer getAmountScale() {
        return amountScale;
    }

    public void setAmountScale(Integer amountScale) {
        this.amountScale = amountScale;
    }

    public Integer getBigNumberStart() {
        return bigNumberStart;
    }

    public void setBigNumberStart(Integer bigNumberStart) {
        this.bigNumberStart = bigNumberStart;
    }

    public Integer getBigNumberWidth() {
        return bigNumberWidth;
    }

    public void setBigNumberWidth(Integer bigNumberWidth) {
        this.bigNumberWidth = bigNumberWidth;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
    
    /**
     * 
     * outside API notional limits ($1 - $1,000,000)
     * @param counter
     * @return
     */
    public static BigDecimal getQuantity(String counter) {
        if (counter == null || counter.trim().isEmpty()) {
            return null;
        }
        if (counter.equals("JPY")) {
            return BigDecimal.valueOf(1000d);
        }
        if (counter.equals("BTC")) {
            return BigDecimal.valueOf(0.5d);
        }
        if (counter.equals("BCH")) {
            return BigDecimal.valueOf(50d);
        }
        if (counter.equals("ETH")) {
            return BigDecimal.valueOf(50d);
        }
        if (counter.equals("XRP")) {
            return BigDecimal.valueOf(50000d);
        }
        if (counter.equals("LTC")) {
            return BigDecimal.valueOf(50d);
        }
        return BigDecimal.valueOf(0.01d);
    }

}
