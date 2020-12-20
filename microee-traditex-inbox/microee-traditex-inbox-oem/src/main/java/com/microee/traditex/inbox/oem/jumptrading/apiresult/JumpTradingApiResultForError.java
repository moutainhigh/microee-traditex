package com.microee.traditex.inbox.oem.jumptrading.apiresult;

import java.io.Serializable;
import com.microee.plugin.http.assets.HttpAssets;

public class JumpTradingApiResultForError extends JumpTradingApiResultBase implements Serializable {
    
    private static final long serialVersionUID = -7267934855282462150L;

    private Integer error;
    private String description;
    private String info;
    private Object extra;

    public Integer getError() {
        return error;
    }

    public void setError(Integer error) {
        this.error = error;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Object getExtra() {
        return extra;
    }

    public void setExtra(Object extra) {
        this.extra = extra;
    }
    
    public static JumpTradingApiResultForError parseResult(String jsonString) {
        if (jsonString == null || jsonString.trim().isEmpty()) {
            return null;
        }
        return HttpAssets.parseJson(jsonString, JumpTradingApiResultForError.class);
    }

}
