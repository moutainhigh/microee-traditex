package com.microee.traditex.inbox.oem.constrants;

import java.util.Arrays;
import java.util.Optional;

public enum VENDER {

    HBiTex("HBG", "HB Global"),
    HBiJTex("HBJ", "HB Japan"),
    JumpTrading("JumpTrading", "JumpTrading"),
    CumberLand("CumberLand", "CumberLand"),
    B2C2("B2C2", "B2C2"),
    Oanda("Oanda", "汇率对冲平台");

    public String code;
    public String desc;

    VENDER(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static VENDER get(final String code) {
        if (code == null || code.trim().isEmpty()) return null;
        Optional<VENDER> o = Arrays.asList(VENDER.values()).stream()
                .filter(p -> p.name().equals(code) || p.code.equals(code)).findFirst();
        if (o.isPresent()) {
            return o.get();
        }
        return null;
    }
}
