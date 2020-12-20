package com.microee.traditex.inbox.oem.oo.vo;

import java.io.Serializable;
import java.math.BigDecimal;

public class QuoteVo implements Serializable {

    private static final long serialVersionUID = -3823723835838159588L;
    
    private String id;
    private String ticker; // 交易对
    private String bos; // 卖或买
    private BigDecimal mny; // 单价
    private BigDecimal cnt; // 数量
    private Long time;
    
    public QuoteVo() {
        
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getBos() {
        return bos;
    }

    public void setBos(String bos) {
        this.bos = bos;
    }

    public BigDecimal getMny() {
        return mny;
    }

    public void setMny(BigDecimal mny) {
        this.mny = mny;
    }

    public BigDecimal getCnt() {
        return cnt;
    }

    public void setCnt(BigDecimal cnt) {
        this.cnt = cnt;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
    
}
