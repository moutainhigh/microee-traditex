package com.microee.traditex.inbox.oem.oo.vo;

import java.io.Serializable;
import java.math.BigDecimal;

public class QuoteAggVo implements Serializable {

    private static final long serialVersionUID = -3823723835838159588L;
    
    private String id;
    private String ticker; // 交易对
    private BigDecimal bidsSumMny; // 10档挂单单价总和
    private BigDecimal bidsSumCnt; // 10档挂单数量总和
    private BigDecimal bidsSumMnyAvg; // 10档挂单单价平均值
    private BigDecimal bidsSumCntAvg; // 10档挂单数量总和平均值
    private BigDecimal asksSumMny; // 10档挂单单价总和
    private BigDecimal asksSumCnt; // 10档挂单数量总和
    private BigDecimal asksSumMnyAvg; // 10档挂单单价平均值
    private BigDecimal asksSumCntAvg; // 10档挂单数量总和平均值
    private Long time;
    
    public QuoteAggVo() {
        
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

    public BigDecimal getBidsSumMny() {
        return bidsSumMny;
    }

    public void setBidsSumMny(BigDecimal bidsSumMny) {
        this.bidsSumMny = bidsSumMny;
    }

    public BigDecimal getBidsSumCnt() {
        return bidsSumCnt;
    }

    public void setBidsSumCnt(BigDecimal bidsSumCnt) {
        this.bidsSumCnt = bidsSumCnt;
    }

    public BigDecimal getBidsSumMnyAvg() {
        return bidsSumMnyAvg;
    }

    public void setBidsSumMnyAvg(BigDecimal bidsSumMnyAvg) {
        this.bidsSumMnyAvg = bidsSumMnyAvg;
    }

    public BigDecimal getBidsSumCntAvg() {
        return bidsSumCntAvg;
    }

    public void setBidsSumCntAvg(BigDecimal bidsSumCntAvg) {
        this.bidsSumCntAvg = bidsSumCntAvg;
    }

    public BigDecimal getAsksSumMny() {
        return asksSumMny;
    }

    public void setAsksSumMny(BigDecimal asksSumMny) {
        this.asksSumMny = asksSumMny;
    }

    public BigDecimal getAsksSumCnt() {
        return asksSumCnt;
    }

    public void setAsksSumCnt(BigDecimal asksSumCnt) {
        this.asksSumCnt = asksSumCnt;
    }

    public BigDecimal getAsksSumMnyAvg() {
        return asksSumMnyAvg;
    }

    public void setAsksSumMnyAvg(BigDecimal asksSumMnyAvg) {
        this.asksSumMnyAvg = asksSumMnyAvg;
    }

    public BigDecimal getAsksSumCntAvg() {
        return asksSumCntAvg;
    }

    public void setAsksSumCntAvg(BigDecimal asksSumCntAvg) {
        this.asksSumCntAvg = asksSumCntAvg;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
    
}
