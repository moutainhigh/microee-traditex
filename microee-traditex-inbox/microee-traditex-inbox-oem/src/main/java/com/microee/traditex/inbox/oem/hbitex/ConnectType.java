package com.microee.traditex.inbox.oem.hbitex;

import java.util.Arrays;
import java.util.Optional;

public enum ConnectType {

    ORDER_BOOK("ORDER_BOOK", "订单薄连接"),
    ORDER_BALANCE("ORDER_BALANCE", "资产及订单连接"),
    OANDA_PRICE("OANDA_PRICE", "汇率连接");

    private String code;
    private String desc;

    ConnectType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static ConnectType get(final String code) {
        if (code == null || code.trim().isEmpty()) return null;
        Optional<ConnectType> o = Arrays.asList(ConnectType.values()).stream()
                .filter(p -> p.code.equals(code)).findFirst();
        if (o.isPresent()) {
            return o.get();
        }
        return null;
    }
}
