package com.microee.traditex.inbox.oem.constrants;

import java.util.Arrays;
import java.util.Optional;

public enum SymbolEnums {

    USDJPY("usdjpy", "USD/JPY"),
    BTCJPY("btcjpy", "BTC/JPY"),
    BTCUSDT("btcusdt", "BTC/USDT");

    public String code;
    public String title;

    SymbolEnums(String code, String title) {
        this.code = code;
        this.title = title;
    }

    public static SymbolEnums get(final String code) {
        if (code == null || code.trim().isEmpty()) return null;
        Optional<SymbolEnums> o = Arrays.asList(SymbolEnums.values()).stream()
                .filter(p -> p.code.equals(code)).findFirst();
        return o.isPresent() ? o.get() : null;
    }
    
}
