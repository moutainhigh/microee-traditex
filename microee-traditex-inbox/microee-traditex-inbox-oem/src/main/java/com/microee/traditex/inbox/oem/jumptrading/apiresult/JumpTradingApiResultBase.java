package com.microee.traditex.inbox.oem.jumptrading.apiresult;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.microee.plugin.http.assets.HttpAssets;

public class JumpTradingApiResultBase implements Serializable {

    private static final long serialVersionUID = -7267934855282462150L;

    @JsonProperty("BeginString")
    private String beginString;
    @JsonProperty("MsgType")
    private String msgType;
    @JsonProperty("MsgSeqNum")
    private Long msgSeqNum;
    @JsonProperty("SendingTime")
    private String sendingTime;
    
    public JumpTradingApiResultBase() {
        
    }

    public String getBeginString() {
        return beginString;
    }

    public void setBeginString(String beginString) {
        this.beginString = beginString;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public Long getMsgSeqNum() {
        return msgSeqNum;
    }

    public void setMsgSeqNum(Long msgSeqNum) {
        this.msgSeqNum = msgSeqNum;
    }

    public String getSendingTime() {
        return sendingTime;
    }

    public void setSendingTime(String sendingTime) {
        this.sendingTime = sendingTime;
    }

    @JsonIgnore
    public boolean success() {
        return !this.msgType.equals("REST_MSG_TYPE_ERROR");
    }
    
    public static JumpTradingApiResultBase parseResult(String jsonString) {
        if (jsonString == null || jsonString.trim().isEmpty()) {
            return null;
        }
        return HttpAssets.parseJson(jsonString, JumpTradingApiResultForError.class);
    }

}
