package com.microee.traditex.inbox.oem.constrants;

import java.util.Arrays;
import java.util.Optional;

public enum OrderSideEnum {

    BUY("buy", "卖"),
    SELL("sell", "买");

    public String code;
    public String desc;

    OrderSideEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static OrderSideEnum get(final String code) {
        if (code == null || code.trim().isEmpty()) return null;
        Optional<OrderSideEnum> o = Arrays.asList(OrderSideEnum.values()).stream()
                .filter(p -> p.code.equals(code)).findFirst();
        if (o.isPresent()) {
            return o.get();
        }
        return null;
    }
    
}
