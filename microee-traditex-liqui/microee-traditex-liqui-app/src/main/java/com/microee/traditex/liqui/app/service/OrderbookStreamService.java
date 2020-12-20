package com.microee.traditex.liqui.app.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.microee.plugin.http.assets.HttpAssets;
import com.microee.traditex.inbox.oem.constrants.OrderSideEnum;
import com.microee.traditex.inbox.oem.constrants.VENDER;
import com.microee.traditex.liqui.app.mappers.OrderbookStreamMapper;
import com.microee.traditex.liqui.oem.OrderBook;
import com.microee.traditex.liqui.oem.enums.OrderBookSkipCodeEnum;
import com.microee.traditex.liqui.oem.models.OrderbookStream;

@Service
public class OrderbookStreamService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderbookStreamService.class);

    @Autowired
    private OrderbookStreamMapper orderbookStreamMapper;

    public boolean save(VENDER vender, OrderBookSkipCodeEnum skipCode,
            OrderBook orderbook) {
        String orderBookId = orderbook.orderBookId;
        List<OrderbookStream> recordList = new ArrayList<>();
        for (int i = 0; i < orderbook.asks.length(); i++) {
            if (skipCode.equals(OrderBookSkipCodeEnum.SKIPED_IF_BUY_NOT_OPEN) && !skipCode.equals(OrderBookSkipCodeEnum.SKIPED_IF_SELL_NOT_OPEN)) {
                recordList.add(this.getRecord(orderBookId, i, vender, OrderBookSkipCodeEnum.FOUCED, OrderSideEnum.SELL, orderbook.symbol, orderbook.asks));
            } else {
                recordList.add(this.getRecord(orderBookId, i, vender, skipCode, OrderSideEnum.SELL, orderbook.symbol, orderbook.asks));
            }
        }
        for (int i = 0; i < orderbook.bids.length(); i++) {
            if (skipCode.equals(OrderBookSkipCodeEnum.SKIPED_IF_SELL_NOT_OPEN) && !skipCode.equals(OrderBookSkipCodeEnum.SKIPED_IF_BUY_NOT_OPEN)) {
                recordList.add(this.getRecord(orderBookId, i, vender, OrderBookSkipCodeEnum.FOUCED, OrderSideEnum.BUY, orderbook.symbol, orderbook.bids));
            } else {
                recordList.add(this.getRecord(orderBookId, i, vender, skipCode, OrderSideEnum.BUY, orderbook.symbol, orderbook.bids));
            }
        }
        try {
            // 批量存入多条
            recordList = recordList.stream().filter(f -> f != null).collect(Collectors.toList());
            if (recordList.size() > 0) {
                orderbookStreamMapper.insertList(recordList);
            }
            return true;
        } catch (Exception e) {
            LOGGER.error("errorMessage={}, list={}", e.getMessage(),
                    HttpAssets.toJsonString(recordList));
            return false;
        }
    }

    public OrderbookStream getRecord(String orderBookId, int i, VENDER vender,
            OrderBookSkipCodeEnum skipCode, OrderSideEnum side, String symbol, JSONArray items) {
        OrderbookStream record = null;
        if (items != null && items.length() > 0) {
            JSONArray array = items.getJSONArray(i);
            if (array.length() != 2) {
                return null;
            }
            record = new OrderbookStream();
            record.setOrderBookId(String.format("%s-%03d-%s", orderBookId, i, side.name()));
            record.setBookId(orderBookId);
            record.setSymbol(symbol);
            record.setSide(side.name());
            record.setAmount(BigDecimal.valueOf(array.getDouble(1)));
            record.setPrice(BigDecimal.valueOf(array.getDouble(0))); 
            record.setVender(vender.code);
            record.setCalType(1);
            record.setPick(i);
            record.setSkipCode(skipCode.code);
            record.setEmitterTime(new Date(Instant.now().toEpochMilli()));
            record.setReveiveTime(new Date(Instant.now().toEpochMilli()));
            record.setCreatedAt(new Date(Instant.now().toEpochMilli()));
        }
        return record;
    }
    
}
