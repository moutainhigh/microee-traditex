package com.microee.traditex.inbox.oem.b2c2.apiresult;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class B2C2ApiResultForInstruments implements Serializable {

    private static final long serialVersionUID = -2149477934975143688L;

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static Class<?> listType() {
        List<B2C2ApiResultForInstruments> apiResult2 = new ArrayList<>();
        return apiResult2.getClass();
    }
    
}
