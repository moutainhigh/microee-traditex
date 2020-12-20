package com.microee.traditex.inbox.oem.jumptrading.apiresult;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.microee.plugin.http.assets.HttpAssets;

public class JumpTradingApiResultForConnected extends JumpTradingApiResultBase
        implements Serializable {

    private static final long serialVersionUID = -5520785189190897501L;

    @JsonProperty("EncryptMethod")
    private Integer encryptMethod;

    @JsonProperty("HeartbeatInt")
    private Integer heartbeatInt;

    public Integer getEncryptMethod() {
        return encryptMethod;
    }

    public void setEncryptMethod(Integer encryptMethod) {
        this.encryptMethod = encryptMethod;
    }

    public Integer getHeartbeatInt() {
        return heartbeatInt;
    }

    public void setHeartbeatInt(Integer heartbeatInt) {
        this.heartbeatInt = heartbeatInt;
    }
    
    public static JumpTradingApiResultForConnected parseLine(String line) {
        return HttpAssets.parseJson(line, JumpTradingApiResultForConnected.class);
    }

}
