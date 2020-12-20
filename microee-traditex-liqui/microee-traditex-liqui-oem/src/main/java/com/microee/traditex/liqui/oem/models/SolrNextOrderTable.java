package com.microee.traditex.liqui.oem.models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class SolrNextOrderTable implements Serializable {
    private String nextSolrId;

    private String solrClientOrderId;

    private String solrSymbol;

    private String solrSide;

    private String solrOrderType;

    private BigDecimal nextSolrQuantity;

    private String diskResultOrderId;

    private String diskSymbol;

    private Date createdAt;

    private static final long serialVersionUID = 1L;

    public String getNextSolrId() {
        return nextSolrId;
    }

    public void setNextSolrId(String nextSolrId) {
        this.nextSolrId = nextSolrId == null ? null : nextSolrId.trim();
    }

    public String getSolrClientOrderId() {
        return solrClientOrderId;
    }

    public void setSolrClientOrderId(String solrClientOrderId) {
        this.solrClientOrderId = solrClientOrderId == null ? null : solrClientOrderId.trim();
    }

    public String getSolrSymbol() {
        return solrSymbol;
    }

    public void setSolrSymbol(String solrSymbol) {
        this.solrSymbol = solrSymbol == null ? null : solrSymbol.trim();
    }

    public String getSolrSide() {
        return solrSide;
    }

    public void setSolrSide(String solrSide) {
        this.solrSide = solrSide == null ? null : solrSide.trim();
    }

    public String getSolrOrderType() {
        return solrOrderType;
    }

    public void setSolrOrderType(String solrOrderType) {
        this.solrOrderType = solrOrderType == null ? null : solrOrderType.trim();
    }

    public BigDecimal getNextSolrQuantity() {
        return nextSolrQuantity;
    }

    public void setNextSolrQuantity(BigDecimal nextSolrQuantity) {
        this.nextSolrQuantity = nextSolrQuantity;
    }

    public String getDiskResultOrderId() {
        return diskResultOrderId;
    }

    public void setDiskResultOrderId(String diskResultOrderId) {
        this.diskResultOrderId = diskResultOrderId == null ? null : diskResultOrderId.trim();
    }

    public String getDiskSymbol() {
        return diskSymbol;
    }

    public void setDiskSymbol(String diskSymbol) {
        this.diskSymbol = diskSymbol == null ? null : diskSymbol.trim();
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}