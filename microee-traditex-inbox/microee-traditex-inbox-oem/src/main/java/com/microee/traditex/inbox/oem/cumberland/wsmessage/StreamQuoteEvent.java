package com.microee.traditex.inbox.oem.cumberland.wsmessage;

import java.io.Serializable;
import com.microee.traditex.inbox.oem.cumberland.entity.CBExternalIdentity;
import com.microee.traditex.inbox.oem.cumberland.entity.CBQuantity;
import com.microee.traditex.inbox.oem.cumberland.entity.CBResponseId;
import com.microee.traditex.inbox.oem.cumberland.entity.CBTotalAmount;
import com.microee.traditex.inbox.oem.cumberland.entity.CBUnitPrice;
import com.microee.traditex.inbox.oem.cumberland.inter.QuoteRequest;

public class StreamQuoteEvent extends StreamEventBase implements Serializable {

    private static final long serialVersionUID = -6383989739050649320L;
    public static final String _EVENT_TYPE = "STREAMING_QUOTE_RESPONSE";

    private String counterpartyRequestId;
    private CBResponseId quoteResponseId;
    private QuoteRequest quoteRequest;
    private Long creationTime;
    private Integer timeToLive;
    private Long expireTime;
    private CBQuantity buyQuantity;
    private CBUnitPrice buyUnitPrice;
    private CBTotalAmount buyTotalAmount;
    private CBQuantity sellQuantity;
    private CBUnitPrice sellUnitPrice;
    private CBTotalAmount sellTotalAmount;
    private CBExternalIdentity externalIdentity;
    private String reason;

    public StreamQuoteEvent() {

    }

    public String getCounterpartyRequestId() {
        return counterpartyRequestId;
    }

    public void setCounterpartyRequestId(String counterpartyRequestId) {
        this.counterpartyRequestId = counterpartyRequestId;
    }

    public CBResponseId getQuoteResponseId() {
        return quoteResponseId;
    }

    public void setQuoteResponseId(CBResponseId quoteResponseId) {
        this.quoteResponseId = quoteResponseId;
    }

    public QuoteRequest getQuoteRequest() {
        return quoteRequest;
    }

    public void setQuoteRequest(QuoteRequest quoteRequest) {
        this.quoteRequest = quoteRequest;
    }

    public Long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Long creationTime) {
        this.creationTime = creationTime;
    }

    public Integer getTimeToLive() {
        return timeToLive;
    }

    public void setTimeToLive(Integer timeToLive) {
        this.timeToLive = timeToLive;
    }

    public Long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Long expireTime) {
        this.expireTime = expireTime;
    }

    public CBQuantity getBuyQuantity() {
        return buyQuantity;
    }

    public void setBuyQuantity(CBQuantity buyQuantity) {
        this.buyQuantity = buyQuantity;
    }

    public CBUnitPrice getBuyUnitPrice() {
        return buyUnitPrice;
    }

    public void setBuyUnitPrice(CBUnitPrice buyUnitPrice) {
        this.buyUnitPrice = buyUnitPrice;
    }

    public CBTotalAmount getBuyTotalAmount() {
        return buyTotalAmount;
    }

    public void setBuyTotalAmount(CBTotalAmount buyTotalAmount) {
        this.buyTotalAmount = buyTotalAmount;
    }

    public CBQuantity getSellQuantity() {
        return sellQuantity;
    }

    public CBUnitPrice getSellUnitPrice() {
        return sellUnitPrice;
    }

    public void setSellUnitPrice(CBUnitPrice sellUnitPrice) {
        this.sellUnitPrice = sellUnitPrice;
    }

    public CBTotalAmount getSellTotalAmount() {
        return sellTotalAmount;
    }

    public void setSellTotalAmount(CBTotalAmount sellTotalAmount) {
        this.sellTotalAmount = sellTotalAmount;
    }

    public void setSellQuantity(CBQuantity sellQuantity) {
        this.sellQuantity = sellQuantity;
    }

    public CBExternalIdentity getExternalIdentity() {
        return externalIdentity;
    }

    public void setExternalIdentity(CBExternalIdentity externalIdentity) {
        this.externalIdentity = externalIdentity;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

}
