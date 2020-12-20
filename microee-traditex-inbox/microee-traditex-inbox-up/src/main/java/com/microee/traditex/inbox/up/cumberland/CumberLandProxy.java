package com.microee.traditex.inbox.up.cumberland;

import java.math.BigDecimal;
import com.fasterxml.jackson.core.type.TypeReference;
import com.microee.plugin.http.assets.HttpAssets;
import com.microee.plugin.response.R;
import com.microee.plugin.response.exception.RestException;
import com.microee.traditex.inbox.oem.cumberland.apiparam.CBParamBase;
import com.microee.traditex.inbox.oem.cumberland.apiparam.CBReferenceDataParam;
import com.microee.traditex.inbox.oem.cumberland.apiresult.CBApiResultForReferenceData;
import com.microee.traditex.inbox.oem.cumberland.apiresult.CBApiResultForStatus;
import com.microee.traditex.inbox.oem.cumberland.apiresult.CBApiResultForTime;
import com.microee.traditex.inbox.oem.cumberland.apiresult.CBTickers;
import com.microee.traditex.inbox.oem.cumberland.entity.CBAsyncResult;
import com.microee.traditex.inbox.oem.cumberland.entity.CBResponseId;
import com.microee.traditex.inbox.oem.cumberland.wsmessage.CBTradeHistoryRequestMessage;
import com.microee.traditex.inbox.oem.cumberland.wsmessage.CBTradeHistoryResponseEvent;
import com.microee.traditex.inbox.oem.cumberland.wsmessage.CBTradeRequestMessage;
import com.microee.traditex.inbox.oem.cumberland.wsmessage.CBTradeRequestParam;
import com.microee.traditex.inbox.oem.cumberland.wsmessage.CBTradeResponseEvent;
import com.microee.traditex.inbox.oem.cumberland.wsmessage.QuoteCloseRequestMessage;
import com.microee.traditex.inbox.oem.cumberland.wsmessage.QuoteRequestMessage;
import com.microee.traditex.inbox.oem.cumberland.wsmessage.StreamQuoteEvent;

public class CumberLandProxy {

    private final CumberLandFactory factory;

    public CumberLandProxy(CumberLandFactory factory) {
        this.factory = factory;
    }

    // 获取时间
    public CBApiResultForTime getTime() {
        return this.factory.get("CumberLand获取时间", "/time", null,
                new TypeReference<CBApiResultForTime>() {});
    }

    // 获取状态
    public CBApiResultForStatus getStatus() {
        return this.factory.get("cb获取状态", "/status", new CBParamBase().queryParam(),
                new TypeReference<CBApiResultForStatus>() {});
    }

    // 获取cb交易所最新币对
    public CBApiResultForReferenceData getNewSymbolNames() {
        return this.factory.get("cb交易所最新币对", "/reference_data",
                new CBReferenceDataParam(this.factory.counterPartyId).queryParam(),
                new TypeReference<CBApiResultForReferenceData>() {});
    }

    // 询价,订阅交易对: BTC_USDT
    public StreamQuoteEvent streamingQuoteRequest(String baseCurrency, String quoteCurrency) {
        final String counterPartyId = this.factory.counterPartyId;
        final String userId = this.factory.userId;
        final String ticker = String.format("%s_%s", baseCurrency, quoteCurrency);
        final QuoteRequestMessage quoteRequestMessage = new QuoteRequestMessage(ticker,
                quoteCurrency, CBTickers.getQuantity(quoteCurrency), counterPartyId, userId);
        return this.factory.orderBookStreamHandler().sendMessageSync(quoteRequestMessage.json(),
                quoteRequestMessage.getCounterpartyRequestId(),
                new CBAsyncResult<StreamQuoteEvent>());
    }

    // 取消订阅交易对: BTC_USDT
    public String unsubscribe(String quoteResponstId) {
        final String counterPartyId = this.factory.counterPartyId;
        final String userId = this.factory.userId;
        CBResponseId responseId = new CBResponseId(quoteResponstId);
        QuoteCloseRequestMessage quoteCloseRequestMessage =
                new QuoteCloseRequestMessage(counterPartyId, userId, responseId);
        this.factory.orderBookStreamHandler().sendMessage(quoteCloseRequestMessage.json());
        return responseId.getId();
    }

    // 查询交易历史, queryCount <= 500
    public CBTradeHistoryResponseEvent queryTradHistory(int queryCount) {
        final String counterPartyId = this.factory.counterPartyId;
        final String userId = this.factory.userId;
        final CBTradeHistoryRequestMessage tradeHistoryRequestMessage =
                new CBTradeHistoryRequestMessage(queryCount, counterPartyId, userId);
        final CBAsyncResult<CBTradeHistoryResponseEvent> asyncResult = new CBAsyncResult<>();
        final CBTradeHistoryResponseEvent tradeHistoryResponseEvent =
                this.factory.orderBookStreamHandler().sendMessageSync(
                        HttpAssets.toJsonString(tradeHistoryRequestMessage),
                        tradeHistoryRequestMessage.getCounterpartyRequestId(), asyncResult);
        if (tradeHistoryResponseEvent == null) {
            throw new RestException(R.TIME_OUT, "查询超时");
        }
        return tradeHistoryResponseEvent;
    }

    // 下单
    public CBTradeResponseEvent createOrder(String baseCurrency, String quoteCurrency,
            CBTradeRequestParam tradeRequestParam) {
        final String counterPartyId = this.factory.counterPartyId;
        final String userId = this.factory.userId;
        // 获取用于拿的报价参数对象
        final QuoteRequestMessage quoteRequestMessage = tradeRequestParam.getQuoteParam(
                baseCurrency, quoteCurrency, "TRAD-QUOTE-" + tradeRequestParam.getClOrdId(), counterPartyId, userId,
                tradeRequestParam.getQuantity());
        final CBAsyncResult<StreamQuoteEvent> asyncResult = new CBAsyncResult<>();
        // 拿报价
        final StreamQuoteEvent streamQuoteEvent =
                this.factory.orderBookStreamHandler().sendMessageSync(quoteRequestMessage.json(),
                        quoteRequestMessage.getCounterpartyRequestId(), asyncResult);
        if (streamQuoteEvent == null) {
            throw new RestException(R.TIME_OUT, "下单获取报价超时");
        }
        if (streamQuoteEvent.getStatus().equals("REJECTED")) {
            throw new RestException(R.FAILED, "下单获取报价被拒绝`" + streamQuoteEvent.getReason() + "`");
        }
        if (!streamQuoteEvent.getStatus().equals("OPEN")) {
            throw new RestException(R.FAILED, "当前交易对状态处理关闭状态");
        }
        if ("2".equals(tradeRequestParam.getOrdType()) && tradeRequestParam.getPrice() != null) {
            // 限价单, 限价处理
            if ("SELL".equals(tradeRequestParam.getSide())) {
                BigDecimal sellPrice = streamQuoteEvent.getSellUnitPrice().getPrice();
                if (tradeRequestParam.getPrice().compareTo(sellPrice) > 0) {
                    throw new RestException(R.FAILED, "下单失败不符合限制价格");
                }
            } else {
                BigDecimal buyPrice = streamQuoteEvent.getBuyUnitPrice().getPrice();
                if (tradeRequestParam.getPrice().compareTo(buyPrice) < 0) {
                    throw new RestException(R.FAILED, "下单失败不符合限制价格");
                }
            }
        }
        String clientOrderId = tradeRequestParam.getClOrdId();
        final CBResponseId responseId = streamQuoteEvent.getQuoteResponseId();
        // 发起交易
        final CBTradeRequestMessage tradeRequest =
                new CBTradeRequestMessage(responseId, tradeRequestParam.getSide(),
                        clientOrderId, counterPartyId, userId);
        CBTradeResponseEvent tradDetail = this.factory.orderBookStreamHandler().sendMessageSync(
                HttpAssets.toJsonString(tradeRequest), clientOrderId, new CBAsyncResult<CBTradeResponseEvent>() {});
        if (tradDetail == null) {
            throw new RestException(R.TIME_OUT, "下单超时需确认订单是否下单成功");
        }
        // 下单成功后取消订阅该报价, 需确认订单成交后是否还在继续推报价 ???? 如继续推需取消订阅
        QuoteCloseRequestMessage quoteCloseRequestMessage =
                new QuoteCloseRequestMessage(counterPartyId, userId, responseId);
        this.factory.orderBookStreamHandler().sendMessage(quoteCloseRequestMessage.json());
        return tradDetail;
    }

}
