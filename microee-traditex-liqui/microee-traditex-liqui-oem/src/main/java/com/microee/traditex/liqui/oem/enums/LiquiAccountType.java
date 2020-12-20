package com.microee.traditex.liqui.oem.enums;

import java.util.Arrays;
import java.util.Optional;

public enum LiquiAccountType {

    SOLR(0, "盘面对冲账户"),
    DISK(1, "流动性账户"),
    HDEG(2, "汇率对冲账户"),;

    public Integer code;
    public String desc;

    LiquiAccountType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static LiquiAccountType get(final Integer code) {
        if (code == null)
            return null;
        Optional<LiquiAccountType> o = Arrays.asList(LiquiAccountType.values()).stream()
                .filter(p -> p.code == code).findFirst();
        if (o.isPresent()) {
            return o.get();
        }
        return null;
    }
    
}
