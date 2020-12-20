package com.microee.traditex.liqui.app.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.microee.traditex.inbox.oem.constrants.VENDER;
import com.microee.traditex.inbox.oem.hbitex.vo.HBiTexOrderDetails;
import com.microee.traditex.liqui.app.mappers.DiskOrderDetailMapper;
import com.microee.traditex.liqui.oem.models.DiskOrderDetail;

@Service
public class DiskOrderDetailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiskOrderDetailService.class);
    
    @Autowired
    private DiskOrderDetailMapper diskOrderDetailMapper;
    
    public boolean save(JSONObject diskOrderResult) {
        String diskResultOrderId = diskOrderResult.getLong("order-id") + "";
        String clientOrderId = diskOrderResult.getString("client-order-id") + "";
        String detailId = String.format("%s-%s", clientOrderId, diskOrderResult.getLong("match-id"));
        String matchId = diskOrderResult.has("match-id") ? String.valueOf(diskOrderResult.getLong("match-id")) : null;
        String symbol = diskOrderResult.getString("symbol");
        String orderType = diskOrderResult.getString("order-type");
        String filledAmount = diskOrderResult.getString("filled-amount");
        String filledCashAmount = diskOrderResult.getString("filled-cash-amount");
        String price = diskOrderResult.getString("price");
        String orderState = diskOrderResult.getString("order-state");
        String unfilledAmount = diskOrderResult.getString("unfilled-amount");
        try {
            DiskOrderDetail diskOrder = new DiskOrderDetail();
            diskOrder.setDiskDetailId(detailId);
            diskOrder.setDiskClientOrderId(clientOrderId);
            diskOrder.setDiskResultOrderId(diskResultOrderId);
            diskOrder.setVender(VENDER.HBiJTex.code);
            diskOrder.setMatchId(matchId);
            diskOrder.setDiskOrderSymbol(symbol);
            diskOrder.setDiskFilledAmount(new BigDecimal(filledAmount));
            diskOrder.setDiskFilledPrice(new BigDecimal(price));
            diskOrder.setFilledCashAmount(new BigDecimal(filledCashAmount));
            diskOrder.setDiskOrderState(orderState);
            diskOrder.setDiskOrderType(orderType);
            diskOrder.setUnfilledAmount(new BigDecimal(unfilledAmount));
            diskOrder.setEmitterTime(new Date(Instant.now().toEpochMilli()));
            diskOrder.setCreatedAt(new Date(Instant.now().toEpochMilli()));
            diskOrderDetailMapper.insertSelective(diskOrder);
        } catch (Exception e) {
            LOGGER.error("保存订单详情异常: errorMessage={}", e.getMessage(), e);
            return false;
        }
        return true;
    }

    public void save(HBiTexOrderDetails orderDetails) {
        String clientOrderId = orderDetails.getClientOrderId();
        String detailId = String.format("%s-%s", clientOrderId, orderDetails.getFinishedAt());
        String symbol = orderDetails.getSymbol();
        String orderType = orderDetails.getType();
        String diskOrderId = orderDetails.getId();
        String filledAmount = orderDetails.getFieldAmount();
        String filledCashAmount = orderDetails.getFieldCashAmount();
        try {
            DiskOrderDetail diskOrder = new DiskOrderDetail();
            diskOrder.setDiskDetailId(detailId);
            diskOrder.setDiskClientOrderId(clientOrderId);
            diskOrder.setDiskResultOrderId(diskOrderId);
            diskOrder.setVender(VENDER.HBiJTex.code);
            diskOrder.setDiskOrderSymbol(symbol);
            diskOrder.setDiskFilledAmount(new BigDecimal(filledAmount));
            diskOrder.setDiskFilledPrice(new BigDecimal(orderDetails.getPrice()));
            diskOrder.setFilledCashAmount(new BigDecimal(filledCashAmount));
            diskOrder.setDiskOrderState(orderDetails.getState());
            diskOrder.setDiskOrderType(orderType);
            diskOrder.setUnfilledAmount(null);
            diskOrder.setEmitterTime(new Date(Instant.now().toEpochMilli()));
            diskOrder.setCreatedAt(new Date(Instant.now().toEpochMilli()));
            diskOrderDetailMapper.insertSelective(diskOrder);
        } catch (Exception e) {
            LOGGER.error("保存订单详情异常: errorMessage={}", e.getMessage(), e);
        }
    }

}
