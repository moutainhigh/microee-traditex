package com.microee.traditex.liqui.app.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.type.TypeReference;
import com.microee.plugin.commons.UUIDObject;
import com.microee.traditex.inbox.oem.constrants.VENDER;
import com.microee.traditex.liqui.app.core.conn.ConnectServiceMap;
import com.microee.traditex.liqui.app.mappers.SolrOrderDetailMapper;
import com.microee.traditex.liqui.app.props.conf.HBiTexAccountConf;
import com.microee.traditex.liqui.oem.models.SolrOrderDetail;

@Service 
public class SolrOrderDetailService {

    @Autowired
    private SolrOrderDetailMapper solrOrderDetailMapper;

    @Autowired
    private ConnectServiceMap connectServiceMap;
    
    public void save(String connid, JSONObject orderDetails) {
        HBiTexAccountConf accountConf = connectServiceMap.getAccountByConnId(connid,
                new TypeReference<HBiTexAccountConf>() {});
        String orderId = orderDetails.getLong("order-id") + "";
        String orderStat = orderDetails.getString("order-state");
        String symbol = orderDetails.getString("symbol");
        String orderType = orderDetails.getString("order-type");
        String filledAmount = orderDetails.getString("filled-amount");
        String filledPrice = orderDetails.getString("price");
        String filledCashAmount = orderDetails.getString("filled-cash-amount");
        String unfilledAmount = orderDetails.getString("unfilled-amount");
        String solrClientOrderId = orderDetails.getString("client-order-id");
        SolrOrderDetail solrOrderDetail = new SolrOrderDetail();
        solrOrderDetail.setSolrDetailId(UUIDObject.get().toString());
        solrOrderDetail.setSolrClientOrderId(solrClientOrderId);
        solrOrderDetail.setSolrResultOrderId(orderId);
        solrOrderDetail.setVender(VENDER.HBiTex.code);
        solrOrderDetail.setSolrAccount(accountConf.getUid());
        solrOrderDetail.setSolrOrderSymbol(symbol);
        solrOrderDetail.setSolrFilledAmount(new BigDecimal(filledAmount));
        solrOrderDetail.setSolrFilledPrice(new BigDecimal(filledPrice));
        solrOrderDetail.setFilledCashAmount(new BigDecimal(filledCashAmount));
        solrOrderDetail.setSolrOrderState(orderStat);
        solrOrderDetail.setSolrOrderType(orderType);
        solrOrderDetail.setUnfilledAmount(new BigDecimal(unfilledAmount));
        solrOrderDetail.setEmitterTime(new Date(Instant.now().toEpochMilli()));
        solrOrderDetail.setCreatedAt(new Date(Instant.now().toEpochMilli()));
        solrOrderDetailMapper.insertSelective(solrOrderDetail);
    }
}
