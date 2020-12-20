package com.microee.traditex.liqui.oem.models;

import java.io.Serializable;
import java.util.Date;

public class RevokeOrderRecord implements Serializable {
    private String diskOrderRevokeId;

    private String diskResultOrderId;

    private String vender;

    private Integer revokeOrst;

    private Date startTime;

    private Date endTime;

    private Date createdAt;

    private String errorCode;

    private String errorMessage;

    private String orderBookId;

    private static final long serialVersionUID = 1L;

    public String getDiskOrderRevokeId() {
        return diskOrderRevokeId;
    }

    public void setDiskOrderRevokeId(String diskOrderRevokeId) {
        this.diskOrderRevokeId = diskOrderRevokeId == null ? null : diskOrderRevokeId.trim();
    }

    public String getDiskResultOrderId() {
        return diskResultOrderId;
    }

    public void setDiskResultOrderId(String diskResultOrderId) {
        this.diskResultOrderId = diskResultOrderId == null ? null : diskResultOrderId.trim();
    }

    public String getVender() {
        return vender;
    }

    public void setVender(String vender) {
        this.vender = vender == null ? null : vender.trim();
    }

    public Integer getRevokeOrst() {
        return revokeOrst;
    }

    public void setRevokeOrst(Integer revokeOrst) {
        this.revokeOrst = revokeOrst;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode == null ? null : errorCode.trim();
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage == null ? null : errorMessage.trim();
    }

    public String getOrderBookId() {
        return orderBookId;
    }

    public void setOrderBookId(String orderBookId) {
        this.orderBookId = orderBookId == null ? null : orderBookId.trim();
    }
}