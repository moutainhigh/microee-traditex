package com.microee.traditex.inbox.oem.hbitex;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonProperty;

public class HBiTexHttpResult<T> implements Serializable {

    private static final long serialVersionUID = 6463790663483878901L;

    @JsonProperty("status")
    private String status;
    @JsonProperty("err-code")
    private String errCode;
    @JsonProperty("err-msg")
    private String errMsg;
    @JsonProperty("data")
    private T data;

    @JsonProperty("order-state")
    private Integer orderState;

    public HBiTexHttpResult() {

    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Integer getOrderState() {
        return orderState;
    }

    public void setOrderState(Integer orderState) {
        this.orderState = orderState;
    }

    public boolean isSuccess() {
        return this.status != null && !this.status.equals("error");
    }
    
}