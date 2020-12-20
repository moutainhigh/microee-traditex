package com.microee.traditex.inbox.oem.cumberland.wsmessage;

import java.io.Serializable;
import java.util.List;
import com.microee.traditex.inbox.oem.cumberland.entity.CBExternalIdentity;
import com.microee.traditex.inbox.oem.cumberland.entity.CBResponseId;
import com.microee.traditex.inbox.oem.cumberland.inter.TradeResponse;

public class CBTradeHistoryResponseEvent extends StreamEventBase implements Serializable {

    private static final long serialVersionUID = 3165844580751547135L;

    public static final String MESSAGE_TYPE = "TRADE_HISTORY_RESPONSE";

    private String counterpartyRequestId;
    private CBExternalIdentity externalIdentity;
    private CBResponseId tradeHistoryResponseId;
    private String status;
    private List<TradeResponse> historicalTrades;

    public CBTradeHistoryResponseEvent() {
        
    }

    public String getCounterpartyRequestId() {
        return counterpartyRequestId;
    }

    public void setCounterpartyRequestId(String counterpartyRequestId) {
        this.counterpartyRequestId = counterpartyRequestId;
    }

    public CBExternalIdentity getExternalIdentity() {
        return externalIdentity;
    }

    public void setExternalIdentity(CBExternalIdentity externalIdentity) {
        this.externalIdentity = externalIdentity;
    }

    public CBResponseId getTradeHistoryResponseId() {
        return tradeHistoryResponseId;
    }

    public void setTradeHistoryResponseId(CBResponseId tradeHistoryResponseId) {
        this.tradeHistoryResponseId = tradeHistoryResponseId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<TradeResponse> getHistoricalTrades() {
        return historicalTrades;
    }

    public void setHistoricalTrades(List<TradeResponse> historicalTrades) {
        this.historicalTrades = historicalTrades;
    }

}
