package com.microee.traditex.inbox.oem.cumberland.apiresult;

import java.io.Serializable;

public class CBApiResultForTime extends CBApiResultBase implements Serializable {

    private static final long serialVersionUID = 3324277087878840265L;

    private Long time;

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

}
