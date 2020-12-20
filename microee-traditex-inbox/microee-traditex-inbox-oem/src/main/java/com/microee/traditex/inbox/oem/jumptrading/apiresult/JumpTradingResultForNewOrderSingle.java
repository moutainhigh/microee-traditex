package com.microee.traditex.inbox.oem.jumptrading.apiresult;

import java.io.Serializable;

//will replaced NewOrderSingleResp
public class JumpTradingResultForNewOrderSingle extends JumpTradingApiResultBase implements Serializable {

    private static final long serialVersionUID = -8632009161449443958L;
    
    private String result;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
