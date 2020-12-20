package com.microee.traditex.liqui.oem.enums;

import java.util.Arrays;
import java.util.Optional;

public enum OrderBookSkipCodeEnum {

    FOUCED(0, "没有跳过"),
    SKIPED_IF_HAVE_UNREVOKED(1, "由于盘口有挂单所以跳过该行情"),
    //SKIPED_IF_MONEY_LIMITED(2, "由于盘口资金超过上限所以跳过"),
    //SKIPED_IF_HYSTRIX(3, "由于触发熔断阈值所以跳过"),
    SKIPED_IF_NOT_OPEN(4, "盘口关闭所以跳过"),
    SKIPED_IF_BUY_NOT_OPEN(5, "买盘未开所以跳过"),
    SKIPED_IF_SELL_NOT_OPEN(6, "卖盘未开所以跳过"),
    SKIPED_UNSUPPORTD_SYMBOL(7, "不支持的交易对"),
    SKIPED_AT_WEEKEND(8, "周末停盘");

    public Integer code;
    public String desc;

    OrderBookSkipCodeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static OrderBookSkipCodeEnum get(final Integer code) {
        if (code == null)
            return null;
        Optional<OrderBookSkipCodeEnum> o = Arrays.asList(OrderBookSkipCodeEnum.values()).stream()
                .filter(p -> p.code == code).findFirst();
        if (o.isPresent()) {
            return o.get();
        }
        return null;
    }
    
}
