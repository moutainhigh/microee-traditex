package com.microee.traditex.inbox.oem.cumberland.entity;

public enum CBTradeRequestErrorCode {

    QUOTE_TIMEOUT(12250001, "cb获取报价失败"),
    QUOTE_NOT_OPEN(12250002, "cb报价处于关于状态"),
    QUOTE_MISS_RESPONSEID(12250003, "cb报价缺少QuoteID"),
    SELL_PRICE_INVALID(12250004, "cb不符合限制价格/限卖价"),
    BUY_PRICE_INVALID(12250005, "cb不符合限制价格/限买价"),
    FAILED_TIMEOUT(12250006, "cb下单超时没拿到下单回复"),
    FAILED_NOT_FILLED(12250007, "cb下单失败/状态不是FILLED");

    private Integer code;
    private String desc;

    CBTradeRequestErrorCode(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }


    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
